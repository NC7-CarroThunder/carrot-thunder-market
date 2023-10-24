package bitcamp.carrot_thunder.user.controller;


import bitcamp.carrot_thunder.NcpObjectStorageService;
import bitcamp.carrot_thunder.config.NcpConfig;
import bitcamp.carrot_thunder.dto.ResponseDto;
import bitcamp.carrot_thunder.mail.EmailService;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.dto.LoginRequestDto;
import bitcamp.carrot_thunder.user.dto.PasswdCheckRequestDto;
import bitcamp.carrot_thunder.user.dto.ProfileRequestDto;
import bitcamp.carrot_thunder.user.dto.ProfileResponseDto;
import bitcamp.carrot_thunder.user.dto.SignupRequestDto;
import bitcamp.carrot_thunder.user.dto.UserEmailCheckDto;
import bitcamp.carrot_thunder.user.dto.UserNameCheckDto;
import bitcamp.carrot_thunder.user.service.KakaoService;
import bitcamp.carrot_thunder.user.service.UserService;
import bitcamp.carrot_thunder.post.service.PostService;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {

  // @AllArgsConstructor 를 통한 의존성 주입
  private final NcpConfig ncpConfig;
  private final NcpObjectStorageService ncpObjectStorageService;
  private final KakaoService kakaoService;
  private final UserService userService;
  private final PostService postService;
  private final EmailService emailService;


  // 로그인
  @PostMapping("/users/login")
  @ResponseBody
  public String login(@RequestBody
          LoginRequestDto loginInfo,
          HttpServletResponse response) throws Exception {
    return userService.login(loginInfo,response);
  }

//  @PatchMapping("/users/patch")
//  @ResponseBody
//  public String patch(@AuthenticationPrincipal UserDetailsImpl userDetails,String password) throws Exception {
//    return userService.patchPassword(userDetails, password);
//  }


  // 카카오 로그인 관련 컨트롤러
  @PostMapping("/users/kakao/callback")
  public String kakaoCallback(@RequestBody String access_token, HttpServletResponse response) throws IOException {
    // code : 카카오 서버로부터 받은 인가 코드
   // System.out.println(access_token);

    String nickName = kakaoService.kakaoLogin(access_token, response);
    //String createToken = URLEncoder.encode(kakaoService.kakaoLogin(code, response), "utf-8");
    // Cookie 생성 및 직접 브라우저에 Set
    if (nickName.isEmpty()) {
      //예외처리
    }
    return "응답완료";

  }

  // 회원가입
  @PostMapping("/users/signup")
  @ResponseBody
  public String signup(@RequestBody @Valid SignupRequestDto signupRequestDto, HttpServletResponse response) throws Exception {
    return userService.signup(signupRequestDto,response);
  }


  @GetMapping("delete")
  public String delete(Long userId, Model model) throws Exception {
    if (userService.delete(userId) == 0) {
      model.addAttribute("refresh", "2;url=../post/list");
      throw new Exception("해당 회원이 없습니다.");
    }
    return "redirect:../post/list";
  }


  @GetMapping("list")
  public void list(Model model) throws Exception {
    model.addAttribute("list", userService.list());
  }

//  @GetMapping("profile/{memberId}")
//  public String viewProfile(@PathVariable Long userId, Model model, HttpSession session)
//      throws Exception {
//    User loginUser = (User) session.getAttribute("loginUser");
//    if (loginUser == null) {
//      return "redirect:/user/form";
//    }
//    List<Notification> notifications;
//    if (loginUser.getId() == userId) {
//      notifications = userService.getNotifications(userId);
//    } else {
//      notifications = new ArrayList<>();
//    }
//
//    List<User> followersList;
//    List<User> followingsList;
//
//    if (loginUser.getId() == userId) {
//      followersList = userService.getFollowers(loginUser.getId());
//      followingsList = userService.getFollowings(loginUser.getId());
//    } else {
//      followersList = userService.getFollowers(userId);
//      followingsList = userService.getFollowings(userId);
//    }
//
//    model.addAttribute("followersList", followersList);
//    model.addAttribute("followerCount", followersList.size());
//    model.addAttribute("followingsList", followingsList);
//    model.addAttribute("followingsCount", followingsList.size());
//
//    model.addAttribute("user", userService.get(userId));
//    model.addAttribute("notifications", notifications);
//
//    return "member/profile";
//  }

  // 프로필 유저 정보 단순 조회
  @GetMapping("/profiles/{id}")
  public ResponseDto<ProfileResponseDto> getProfile(@PathVariable long id) throws Exception{
    return ResponseDto.success(userService.getProfile(id));
  }

  // 프로필 유저 정보 세부 조회
  @GetMapping("/profiles")
  public ResponseDto<ProfileResponseDto> getProfileDetail(@AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception{
    System.out.println(userDetails.getUser().getId()); // 넘어오는 값 확인
    return ResponseDto.success(userService.getProfileDetail(userDetails));
  }

  // 프로필 유저 정보 업데이트
  @PutMapping("/profiles")
  public ResponseDto<ProfileRequestDto> updateProfile(
          @AuthenticationPrincipal UserDetailsImpl userDetails,
          @RequestPart ProfileRequestDto profileRequestDto,
          @RequestPart(name = "multipartFile", required = false) MultipartFile multipartFile,
          HttpServletResponse response) throws Exception {
    return ResponseDto.success(userService.updateProfile(userDetails, multipartFile, profileRequestDto, response));
  }

  // 프로필 유저 정보 수정 전 암호 체크
  @GetMapping("/profiles/passwdcheck")
  public String passwdCheck(
          @AuthenticationPrincipal UserDetailsImpl userDetails,
          @RequestBody PasswdCheckRequestDto passwdCheckRequestDto) throws Exception {
    return userService.passwdCheck(userDetails, passwdCheckRequestDto);
  }

  // 프로필 유저 정보 수정 전 닉네임 체크
  @PostMapping("/users/nicknamecheck")
  public ResponseDto<Boolean> userNameCheck (
          @AuthenticationPrincipal UserDetailsImpl userDetails,
          @RequestBody UserNameCheckDto userNameCheckDto,
          HttpServletResponse response) throws Exception {
    return ResponseDto.success(userService.userNameCheck(userDetails, userNameCheckDto, response));
  }


  @PostMapping("/users/useremailcheck")
  public ResponseDto<Boolean> userEmailCheck(
          @AuthenticationPrincipal UserDetailsImpl userDetails,
          @RequestBody UserEmailCheckDto userEmailCheckDto,
          HttpServletResponse response) throws Exception {
    return ResponseDto.success(userService.userEmailCheck(userDetails, userEmailCheckDto, response));
  }

  // Handle MissingServletRequestParameterException and set a custom response status
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<String> handleMissingParam(MissingServletRequestParameterException ex) {
    String paramName = ex.getParameterName();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Required request parameter '" + paramName + "' is not present");
  }


}

