package bitcamp.carrot_thunder.post.controller;

import bitcamp.carrot_thunder.NcpObjectStorageService;
import bitcamp.carrot_thunder.post.dto.PostListResponseDto;
import bitcamp.carrot_thunder.post.dto.PostResponseDto;
import bitcamp.carrot_thunder.post.dto.PostUpdateRequestDto;
import bitcamp.carrot_thunder.dto.ResponseDto;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.service.DefaultNotificationService;
import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;
import bitcamp.carrot_thunder.post.service.PostService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/postApi")
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
    public ResponseDto<String> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseDto.success(postService.deletePost(postId, userDetails.getUser()));
    }



    @GetMapping("/search/posts")
    public ResponseDto<List<PostListResponseDto>> searchPosts(
                                                              @RequestParam(required = false) String keyword,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {


        return ResponseDto.success(postService.searchPosts( keyword, userDetails));
    }
}



