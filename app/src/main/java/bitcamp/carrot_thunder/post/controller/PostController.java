package bitcamp.carrot_thunder.post.controller;

import bitcamp.carrot_thunder.NcpObjectStorageService;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/post")
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

  @DeleteMapping ("/posts/{postID}")
  public String deletePost(@PathVariable int id, HttpSession session) throws Exception {
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/user/form";
    }

    Post p = postService.get(id);

    if (p == null || p.getUser().getId() != loginUser.getId()) {
      throw new Exception("해당 번호의 게시글이 없거나 삭제 권한이 없습니다.");
    } else {
      postService.delete(p.getId());
      return "redirect:/post/list";
    }
  }

    @GetMapping("list")
    public String list(Model model, HttpSession session) throws Exception {
        model.addAttribute("list", postService.list(session));
        return "post/list";
    }


    /** 게시글 상세정보 컨트롤러
     *
     *
     * @param id
     * @return
     * @throws Exception ( 난중에 처리 )
     */

    @GetMapping("detail/{id}")
    public String detail(@PathVariable int id, Model model) throws Exception {

        Post post = postService.getPostDetailById(id);
        model.addAttribute("post", post);
        return "post/detail";

    }

  @PostMapping("update")
  public String update(Post post, MultipartFile[] files, HttpSession session) throws Exception {
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/auth/form";
    }

    Post p = postService.get(post.getId());
    if (p == null || p.getUser().getId() != loginUser.getId()) {
      throw new Exception("게시글이 존재하지 않거나 변경 권한이 없습니다.");
    }

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

    postService.update(post);
    return "redirect:post/list";

  }

  @GetMapping("fileDelete/{attachedFile}")
  public String fileDelete(
      @MatrixVariable("id") int id,
      HttpSession session) throws Exception {

    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/auth/form";
    }

    AttachedFile attachedFile = postService.getAttachedFile(id);
      Post post = postService.get(attachedFile.getPostId());
    if (post.getUser().getId() != loginUser.getId()) {
      throw new Exception("게시글 변경 권한이 없습니다!");
    }

    if (postService.deleteAttachedFile(id) == 0) {
      throw new Exception("해당 번호의 첨부파일이 없다.");
    } else {
      return "redirect:/post/detail/" + "/" + post.getId();
    }
  }







  @PostMapping("/{postId}/like")
  @ResponseBody
  public Map<String, Object> postLike(@PathVariable int postId, HttpSession session)
      throws Exception {
    Map<String, Object> response = new HashMap<>();
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
      response.put("status", "notLoggedIn");
      return response;
    }
    int memberId = loginUser.getId();
    boolean isLiked = postService.postLike(postId, memberId);
    int newLikeCount = postService.getLikeCount(postId);
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
    int memberId = loginUser.getId();
    List<Post> posts = postService.getLikedPosts(memberId, session);
    model.addAttribute("likedPosts", posts);
    return "/post/likeList";
  }

  @PostMapping("/getLikeStatus")
  @ResponseBody
  public Map<Integer, Map<String, Object>> getLikeStatus(@RequestBody List<Integer> postIds,
      HttpSession session)
      throws Exception {
    System.out.println("좋아요 상태 정보 업데이트!");
    User loginUser = (User) session.getAttribute("loginUser");
    Map<Integer, Map<String, Object>> response = new HashMap<>();

    if (loginUser != null) {
      int memberId = loginUser.getId();

      for (int postId : postIds) {
        boolean isLiked = postService.isLiked(postId, memberId);
        int likeCount = postService.getLikeCount(postId);

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



