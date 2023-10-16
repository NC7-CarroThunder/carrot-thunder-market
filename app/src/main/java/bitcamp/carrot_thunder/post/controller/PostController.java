package bitcamp.carrot_thunder.post.controller;

import bitcamp.carrot_thunder.post.dto.PostRequestDto;
import bitcamp.carrot_thunder.post.dto.PostResponseDto;
import bitcamp.carrot_thunder.post.dto.PostUpdateRequestDto;
import bitcamp.carrot_thunder.dto.ResponseDto;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.post.service.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class PostController {


  @Autowired
  PostService postService;



  @GetMapping("/posts/form")
  public void form() {
  }

  @PostMapping("/posts")
  public PostResponseDto add(PostRequestDto postRequestDto, MultipartFile[] files, @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
    return postService.createPost(postRequestDto,files,userDetails);
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

   * @param
   * @return
   */

  @PutMapping("/posts/{postId}")
  public ResponseDto<PostResponseDto> updatePost(
          @PathVariable Long postId,
          @RequestBody PostUpdateRequestDto postUpdateRequestDto,
//            @RequestPart MultipartFile[] files,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {

    return ResponseDto.success((PostResponseDto) postService.updatePost(postId, postUpdateRequestDto, userDetails.getUser() ));
  }

  @DeleteMapping("/posts/{postId}")
  public ResponseDto<Integer> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseDto.success(postService.deletePost(postId, userDetails.getUser()));
  }



}