package bitcamp.carrot_thunder.post.controller;

import bitcamp.carrot_thunder.NcpObjectStorageService;
import bitcamp.carrot_thunder.chatting.service.DefaultNotificationService;
import bitcamp.carrot_thunder.dto.ResponseDto;
import bitcamp.carrot_thunder.jwt.JwtUtil;
import bitcamp.carrot_thunder.post.dto.PostListResponseDto;
import bitcamp.carrot_thunder.post.dto.PostRequestDto;
import bitcamp.carrot_thunder.post.dto.PostResponseDto;
import bitcamp.carrot_thunder.post.dto.PostUpdateRequestDto;
import bitcamp.carrot_thunder.post.dto.WishlistRequest;
import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;
import bitcamp.carrot_thunder.post.service.PostService;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

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

  @Autowired
  JwtUtil jwtUtil;

  @GetMapping("form")
  public void form() {
  }

  @PostMapping("/posts")
  public PostResponseDto add(@RequestPart PostRequestDto postRequestDto,
                             @RequestPart MultipartFile[] multipartFiles,
                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                             HttpServletResponse response) throws Exception {
    createToken(userDetails,response);
    return postService.createPost(postRequestDto, multipartFiles, userDetails);
  }

  @GetMapping("/posts/list")
  public ResponseDto<List<PostListResponseDto>> getAllPosts(
      @AuthenticationPrincipal UserDetailsImpl userDetails,
      String pageNo, String category,
      String word) {

    User user = userDetails != null ? userDetails.getUser() : null;
    if (!word.equals("")) {
      return ResponseDto.success(postService.getPostlistByWord(user, Integer.parseInt(pageNo), word));
    }

    return ResponseDto.success(postService.getPostlist(user, Integer.parseInt(pageNo), category));
  }

  /**
   * 게시글 상세정보 컨트롤러
   *
   * @param postId
   * @param userDetails
   * @return
   */

  @GetMapping("/posts/{postId}")
  public ResponseDto<PostResponseDto> getPost(@PathVariable Long postId,
      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                              HttpServletResponse response) {
    createToken(userDetails,response);
    postService.increaseViewCount(postId);
    return ResponseDto.success(postService.getPost(postId, userDetails));
  }

  /**
   * 게시글 수정 컨트롤러
   *
   * @param postId
   * @param postUpdateRequestDto
   * @param userDetails
   * @return
   */

  @PutMapping("/posts/{postId}")
  public ResponseDto<PostResponseDto> updatePost(
      @PathVariable Long postId,
      @RequestPart PostUpdateRequestDto postUpdateRequestDto,
      @RequestPart(name = "multipartFiles", required = false) MultipartFile[] multipartFiles,
      @AuthenticationPrincipal UserDetailsImpl userDetails,
      HttpServletResponse response) throws Exception {

    createToken(userDetails,response);
//    for (AttachedFile element : postUpdateRequestDto.getAttachedFiles()) {
//      System.out.println("path : " + element.getFilePath());
//    }
    return ResponseDto
        .success((PostResponseDto) postService.updatePost(postId, postUpdateRequestDto,
            userDetails.getUser(), multipartFiles));
  }

  /**
   * 게시글 삭제 컨트롤러
   *
   * @param postId
   * @param userDetails
   * @return
   */


  @DeleteMapping("/posts/{postId}")
  public ResponseDto<Integer> deletePost(@PathVariable Long postId,
      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                         HttpServletResponse response) {
    createToken(userDetails,response);
    return ResponseDto.success(postService.deletePost(postId, userDetails.getUser()));
  }

  @PostMapping("/wishlist/toggle")
  public ResponseEntity<Map<String, Boolean>> toggleWishlist(
      @RequestBody WishlistRequest request,
      @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

    if (userDetailsImpl == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    User user = userDetailsImpl.getUser();
    postService.toggleWishlist(request.getArticleId(), user);
    Map<String, Boolean> response = new HashMap<>();
    response.put("success", true);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/wishlist")
  public ResponseEntity<List<Post>> getUserWishlist(
      @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, HttpServletResponse response) {
    User user = userDetailsImpl.getUser();
    List<Post> wishlist = postService.getUserWishlist(user);
    createToken(userDetailsImpl,response);
    return ResponseEntity.ok(wishlist);
  }

  @GetMapping("/wishlist/status/{postId}")
  public ResponseEntity<Map<String, Boolean>> checkWishlistStatus(
      @PathVariable Long postId,
      @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

    if (userDetailsImpl == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    User user = userDetailsImpl.getUser();
    boolean isInWishlist = postService.isInWishlist(user.getId(), postId);

    Map<String, Boolean> response = new HashMap<>();
    response.put("isLiked", isInWishlist);
    return ResponseEntity.ok(response);
  }

  /**
   * 나의 게시글 조회 컨트롤러
   *
   * @param postId
   * @param userDetails
   * @return
   */


  @GetMapping("/mypost")
  public ResponseDto<List<PostListResponseDto>> getMyPosts(Long postId,
      @AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletResponse response) {
    createToken(userDetails, response );
    return ResponseDto.success(postService.getMyPosts(postId, userDetails));
  }

  // Handle MissingServletRequestParameterException and set a custom response status
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<String> handleMissingParam(MissingServletRequestParameterException ex) {
    String paramName = ex.getParameterName();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Required request parameter '" + paramName + "' is not present");
  }

  private void createToken(UserDetailsImpl userDetails, HttpServletResponse response) {
    if (userDetails != null) {
      User user = userDetails.getUser();
      response.addHeader(JwtUtil.AUTHORIZATION_HEADER,jwtUtil.createToken(user.getNickName(),user.getId(),user.getPoint(), user.getPhoto()));
    }
  }
}
