package bitcamp.carrot_thunder.post.controller;

import bitcamp.carrot_thunder.NcpObjectStorageService;
import bitcamp.carrot_thunder.dto.PostListResponseDto;
import bitcamp.carrot_thunder.dto.PostResponseDto;
import bitcamp.carrot_thunder.dto.PostUpdateRequestDto;
import bitcamp.carrot_thunder.dto.ResponseDto;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.service.DefaultNotificationService;
import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;
import bitcamp.carrot_thunder.post.service.PostService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api")
public class PostController {


  @Autowired
  PostService postService;

  @Autowired
  NcpObjectStorageService ncpObjectStorageService;

  @Autowired
  DefaultNotificationService defaultNotificationService;


  @GetMapping("form")
  public void form() {
  }

  @PostMapping("add")
  public String add(Post post, MultipartFile[] files, HttpSession session) throws Exception {

    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "/user/form";
    }
    post.setUser(loginUser);

    ArrayList<AttachedFile> attachedFiles = new ArrayList<>();
    for (MultipartFile part : files) {
      if (part.getSize() > 0) {
        String uploadFileUrl = ncpObjectStorageService.uploadFile(
            "bitcamp-nc7-bucket-24", "post/", part);
        AttachedFile attachedFile = new AttachedFile();
        attachedFile.setFilePath(uploadFileUrl);
        attachedFiles.add(attachedFile);
      }
    }


    post.setAttachedFiles(attachedFiles);

    postService.add(post);
    return "redirect:/post/list";
  }


    @GetMapping("/list")
    public String list(Model model, HttpSession session) throws Exception {
        model.addAttribute("list", postService.list(session));
        return "post/list";
    }



    /** 게시글 상세정보 컨트롤러
     *
     *
     * @param postId
     * @param userDetails
     * @return
     */


    @GetMapping("/posts/{postId}")
    public ResponseDto<PostResponseDto> getPost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseDto.success(postService.getPost(postId, userDetails));
    }



    /** 게시글 수정 컨트롤러
     *
     *
     * @param postId
     * @param postUpdateRequestDto
     * @param userDetails
     * @param multipartFiles
     * @return
     */

    @PutMapping("/{postId}")
  public ResponseDto<PostResponseDto> updatePost(
          @PathVariable Long postId,
          @RequestBody PostUpdateRequestDto postUpdateRequestDto,
          @RequestParam(required = false) List<MultipartFile> multipartFiles,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {


    return ResponseDto.success((PostResponseDto) postService.updatePost(postId, postUpdateRequestDto, userDetails.getUser(), multipartFiles));
  }

    @DeleteMapping("/posts/{postId}")
    public ResponseDto<Long> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseDto.success(postService.deletePost(postId, userDetails.getUser()));
    }

  @PostMapping("/{postId}/like")
  @ResponseBody
  public Map<String, Object> postLike(@PathVariable Long postId, HttpSession session)
      throws Exception {
    Map<String, Object> response = new HashMap<>();
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
      response.put("status", "notLoggedIn");
      return response;
    }
    Long memberId = loginUser.getId();
    boolean isLiked = postService.postLike(postId, memberId);
    Long newLikeCount = postService.getLikeCount(postId);
    response.put("newIsLiked", isLiked);
    response.put("newLikeCount", newLikeCount);

    // 알림
    if (isLiked) {
      Post post = postService.get(postId);
      if (post != null) {
        String content = loginUser.getNickName() + "님이 당신의 게시글을 좋아합니다.";
        defaultNotificationService.send(content, post.getUser().getId());
      }
    }

    response.put("newIsLiked", isLiked);
    response.put("newLikeCount", newLikeCount);
    return response;
  }

  @GetMapping("/liked")
  public String getLikedPosts(Model model, HttpSession session) throws Exception {
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/member/form";
    }
    Long memberId = loginUser.getId();
    List<Post> posts = postService.getLikedPosts(memberId, session);
    model.addAttribute("likedPosts", posts);
    return "/post/likeList";
  }

  @PostMapping("/getLikeStatus")
  @ResponseBody
  public Map<Long, Map<String, Object>> getLikeStatus(@RequestBody List<Long> postIds,
      HttpSession session)
      throws Exception {
    System.out.println("좋아요 상태 정보 업데이트!");
    User loginUser = (User) session.getAttribute("loginUser");
    Map<Long, Map<String, Object>> response = new HashMap<>();

    if (loginUser != null) {
      Long memberId = loginUser.getId();

      for (Long postId : postIds) {
        boolean isLiked = postService.isLiked(postId, memberId);
        Long likeCount = postService.getLikeCount(postId);

        Map<String, Object> postStatus = new HashMap<>();
        postStatus.put("isLiked", isLiked);
        postStatus.put("likeCount", likeCount);

        response.put(postId, postStatus);
      }
    }
    return response;
  }



    @GetMapping("/search")
    public String searchPosts(@RequestParam(name = "keyword") String keyword, Model model) {
        List<Post> searchResults = postService.searchPosts(keyword);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("keyword", keyword);
        return "/post/search_results";
    }
}



