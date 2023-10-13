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

import bitcamp.carrot_thunder.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class PostController {


  @Autowired
  PostService postService;

  @Autowired
  UserService userService;

  @Autowired
  NcpObjectStorageService ncpObjectStorageService;

  @Autowired
  DefaultNotificationService defaultNotificationService;


  @GetMapping("form")
  public void form() {
  }

  @PostMapping("/posts")
  public PostResponseDto add(@RequestPart PostRequestDto postRequestDto,@RequestPart MultipartFile[] multipartFiles, @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
    return postService.createPost(postRequestDto,multipartFiles,userDetails);
  }

  @GetMapping("/posts/list")
  public ResponseDto<List<PostListResponseDto>> getAllPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    User user = userDetails != null ? userDetails.getUser() : null;
    return ResponseDto.success(postService.getPostlist(user, userDetails));
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



