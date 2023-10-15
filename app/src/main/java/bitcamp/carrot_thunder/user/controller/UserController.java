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
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.model.vo.Notification;
import bitcamp.carrot_thunder.user.model.vo.Role;
import bitcamp.carrot_thunder.user.service.DefaultNotificationService;
import bitcamp.carrot_thunder.user.service.KakaoService;
import bitcamp.carrot_thunder.user.service.UserService;
import bitcamp.carrot_thunder.post.service.PostService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
public class UserController {

  private final EmailService emailService;

  public UserController(UserService userService, EmailService emailService) {
    this.userService = userService;
    this.emailService = emailService;
  }

  @Autowired
  NcpConfig ncpConfig;

  @Autowired
  UserService userService;

  @Autowired
  KakaoService kakaoService;

  @Autowired
  NcpObjectStorageService ncpObjectStorageService;

  @Autowired
  PostService postService;

  @Autowired
  private DefaultNotificationService defaultNotificationService;


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

  @GetMapping("profile/{memberId}")
  public String viewProfile(@PathVariable Long userId, Model model, HttpSession session)
      throws Exception {
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/user/form";
    }
    List<Notification> notifications;
    if (loginUser.getId() == userId) {
      notifications = userService.getNotifications(userId);
    } else {
      notifications = new ArrayList<>();
    }

    List<User> followersList;
    List<User> followingsList;

    if (loginUser.getId() == userId) {
      followersList = userService.getFollowers(loginUser.getId());
      followingsList = userService.getFollowings(loginUser.getId());
    } else {
      followersList = userService.getFollowers(userId);
      followingsList = userService.getFollowings(userId);
    }

    model.addAttribute("followersList", followersList);
    model.addAttribute("followerCount", followersList.size());
    model.addAttribute("followingsList", followingsList);
    model.addAttribute("followingsCount", followingsList.size());

    model.addAttribute("user", userService.get(userId));
    model.addAttribute("notifications", notifications);

    return "member/profile";
  }

//  @GetMapping("detail/{id}")
//  public String detail(@PathVariable Long userId, Model model) throws Exception {
//    model.addAttribute("user", userService.get(userId));
//    return "member/detail";
//  }


  @PostMapping("update")
  public String update(
      User user,
      HttpSession session,
      MultipartFile photofile) throws Exception {

    if (photofile.getSize() > 0) {
      String uploadFileUrl = ncpObjectStorageService.uploadFile(
          "bitcamp-nc7-bucket-16", "member/", photofile);
      user.setPhoto(uploadFileUrl);
    } else {
      // 사용자가 사진을 업로드하지 않은 경우, 기존의 프로필 사진을 그대로 유지하도록 합니다.
      User loginUser = (User) session.getAttribute("loginUser");
      user.setPhoto(loginUser.getPhoto());
    }

    if (userService.update(user) == 0) {
      throw new Exception("회원이 없습니다.");
    } else {
      session.setAttribute("loginUser", user);
      return "redirect:../post/list";
    }

  }

//  @PostMapping("/{memberId}/follow")
//  @ResponseBody
//  public Map<String, Object> memberFollow(@PathVariable Long userId, HttpSession session)
//      throws Exception {
//    Map<String, Object> response = new HashMap<>();
//    User loginUser = (User) session.getAttribute("loginUser");
//
//    if (loginUser == null) {
//      response.put("status", "notLoggedIn");
//      return response;
//    }
//
//    Long currentMemberId = loginUser.getId();
//    boolean newIsFollowed = userService.memberFollow(currentMemberId, userId);
//    response.put("newIsFollowed", newIsFollowed);
//    if (newIsFollowed) {
//      User user = userService.get(userId);
//      if (user != null) {
//        String content = loginUser.getNickName() + "님이 당신을 팔로우했습니다.";
//        defaultNotificationService.send(content, user.getId());
//      }
//    }
//    return response;
//  }
//
////   팔로우 상태 확인
//  @PostMapping("/getFollowStatus")
//  @ResponseBody
//  public Map<Long, Boolean> getFollowStatus(@RequestBody List<Long> userIds,
//      HttpSession session)
//      throws Exception {
//    System.out.println("컨트롤러 팔로우상태확인 호출됨!");
//    User loginUser = (User) session.getAttribute("loginUser");
//    Map<Long, Boolean> response = new HashMap<>();
//    if (loginUser != null) {
//      Long currentMemberId = loginUser.getId();
//      for (Long userId : userIds) {
//        boolean isFollowing = userService.isFollowed(currentMemberId, userId);
//        response.put(userId, isFollowing);
//      }
//    }
//    return response;
//  }
//
//  @GetMapping("/followers")
//  public String followers(HttpSession session, Model model) throws Exception {
//    User loginUser = (User) session.getAttribute("loginUser");
//    if (loginUser == null) {
//      return "redirect:/member/form";
//    }
//    List<User> followersList = userService.getFollowers(loginUser.getId());
//    model.addAttribute("followersList", followersList);
//    model.addAttribute("followerCount", followersList.size());
//    return "member/followers";
//  }
//
//  @GetMapping("/followings")
//  public String followings(HttpSession session, Model model) throws Exception {
//    User loginUser = (User) session.getAttribute("loginUser");
//    if (loginUser == null) {
//      return "redirect:/member/form";
//    }
//    List<User> followingsList = userService.getFollowings(loginUser.getId());
//    model.addAttribute("followingsList", followingsList);
//    model.addAttribute("followingsCount", followingsList.size()); // 팔로잉 숫자 추가
//    return "member/followings";
//  }
//
//  @GetMapping("/notifications/stream")
//  public SseEmitter streamNotifications(HttpSession session) {
//    User loginUser = (User) session.getAttribute("loginUser");
//    if (loginUser == null) {
//      throw new RuntimeException("로그인이 필요합니다.");
//    }
//
//    Long userId = loginUser.getId();
//    return defaultNotificationService.connectNotification(userId);
//  }
//
//  @PostMapping("/notifications/deleteAll")
//  public ResponseEntity<?> deleteAllNotifications(HttpSession session) {
//    User loginUser = (User) session.getAttribute("loginUser");
//    if (loginUser == null) {
//      return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
//    }
//
//    try {
//      userService.deleteAllNotifications(loginUser.getId());
//      return new ResponseEntity<>("모든 알림이 삭제되었습니다.", HttpStatus.OK);
//    } catch (Exception e) {
//      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//  }
//
//  @GetMapping("/headerNotifications")
//  public ResponseEntity<List<Notification>> getHeaderNotifications(HttpSession session) {
//    User loginUser = (User) session.getAttribute("loginUser");
//    if (loginUser == null) {
//      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//    }
//    try {
//      List<Notification> notifications = userService.getNotifications(loginUser.getId());
//      return new ResponseEntity<>(notifications, HttpStatus.OK);
//    } catch (Exception e) {
//      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//  }

  // 프로필 유저 정보 단순 조회
  @GetMapping("/profiles/{id}")
  public ResponseDto<ProfileResponseDto> getProfile(@PathVariable long id) throws Exception{
    return ResponseDto.success(userService.getProfile(id));
  }

  // 프로필 유저 정보 세부 조회
  @GetMapping("/profiles")
  public ResponseDto<ProfileResponseDto> getProfileDetail(@AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception{
//    System.out.println(userDetails.getUser().getId()); // 넘어오는 값 확인
    return ResponseDto.success(userService.getProfileDetail(userDetails));
  }

  // 프로필 유저 정보 업데이트
  @PutMapping("/profiles")
  public ResponseDto<ProfileRequestDto> updateProfile(
          @AuthenticationPrincipal UserDetailsImpl userDetails,
          @RequestBody ProfileRequestDto profileRequestDto,
          @RequestParam(required = false) MultipartFile multipartFile) throws Exception {
    return ResponseDto.success(userService.updateProfile(userDetails, multipartFile, profileRequestDto));
  }

  // 프로필 유저 정보 수정 전 암호 체크
  @GetMapping("/profiles/passwdcheck")
  public String passwdCheck(
          @AuthenticationPrincipal UserDetailsImpl userDetails,
          @RequestBody PasswdCheckRequestDto passwdCheckRequestDto) throws Exception {
    return userService.passwdCheck(userDetails, passwdCheckRequestDto);
  }

}

