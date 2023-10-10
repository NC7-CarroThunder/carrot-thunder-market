package bitcamp.carrot_thunder.user.controller;


import bitcamp.carrot_thunder.NcpObjectStorageService;
import bitcamp.carrot_thunder.config.NcpConfig;
import bitcamp.carrot_thunder.jwt.JwtUtil;
import bitcamp.carrot_thunder.mail.EmailService;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.dto.LoginRequestDto;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

  @PatchMapping("/users/patch")
  @ResponseBody
  public String patch(@AuthenticationPrincipal UserDetailsImpl userDetails,String password) throws Exception {
    return userService.patchPassword(userDetails, password);
  }


  // 카카오 로그인 관련 컨트롤러
  @GetMapping("/users/kakao/callback")
  @ResponseBody
  public void kakaoCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
    // code : 카카오 서버로부터 받은 인가 코드
    String createToken = URLEncoder.encode(kakaoService.kakaoLogin(code, response), "utf-8");
    // Cookie 생성 및 직접 브라우저에 Set
    response.addHeader(JwtUtil.AUTHORIZATION_HEADER,createToken);
    response.sendRedirect("http://localhost:3000");

  }




//  // 로그아웃
//  @GetMapping("/logout")
//  public String logout(HttpSession session) throws Exception {
//
//    session.invalidate();
//    return "redirect:/";
//  }


  // 회원가입
  @PostMapping("/users/signup")
  @ResponseBody
  public String signup(@RequestBody @Valid SignupRequestDto signupRequestDto,  HttpServletResponse response) throws Exception {
    return userService.signup(signupRequestDto,response);
  }
//  @PostMapping("add")
//  public String add(User member) throws Exception {
//    userService.add(member);
//
//    // 회원가입 이메일 전송
//    emailService.sendWelcomeEmail(member);
//
//    return "redirect:form";
//  }

  @GetMapping("delete")
  public String delete(int id, Model model) throws Exception {
    if (userService.delete(id) == 0) {
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
  public String viewProfile(@PathVariable int memberId, Model model, HttpSession session)
      throws Exception {
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/user/form";
    }
    List<Notification> notifications;
    if (loginUser.getId() == memberId) {
      notifications = userService.getNotifications(memberId);
    } else {
      notifications = new ArrayList<>();
    }

    List<User> followersList;
    List<User> followingsList;

    if (loginUser.getId() == memberId) {
      followersList = userService.getFollowers(loginUser.getId());
      followingsList = userService.getFollowings(loginUser.getId());
    } else {
      followersList = userService.getFollowers(memberId);
      followingsList = userService.getFollowings(memberId);
    }

    model.addAttribute("followersList", followersList);
    model.addAttribute("followerCount", followersList.size());
    model.addAttribute("followingsList", followingsList);
    model.addAttribute("followingsCount", followingsList.size());

    model.addAttribute("member", userService.get(memberId));
    model.addAttribute("myPosts", postService.getMyPosts(memberId));
    model.addAttribute("likedPosts", postService.getLikedPosts(memberId, session));
    model.addAttribute("bookMarkedPosts", postService.getBookmarkedPosts(memberId, session));
    model.addAttribute("notifications", notifications);

    return "member/profile";
  }

  @GetMapping("detail/{id}")
  public String detail(@PathVariable int id, Model model) throws Exception {
    model.addAttribute("member", userService.get(id));
    return "member/detail";
  }


  @PostMapping("update")
  public String update(
      User member,
      HttpSession session,
      MultipartFile photofile) throws Exception {

    if (photofile.getSize() > 0) {
      String uploadFileUrl = ncpObjectStorageService.uploadFile(
          "bitcamp-nc7-bucket-16", "member/", photofile);
      member.setPhoto(uploadFileUrl);
    } else {
      // 사용자가 사진을 업로드하지 않은 경우, 기존의 프로필 사진을 그대로 유지하도록 합니다.
      User loginUser = (User) session.getAttribute("loginUser");
      member.setPhoto(loginUser.getPhoto());
    }

    if (userService.update(member) == 0) {
      throw new Exception("회원이 없습니다.");
    } else {
      session.setAttribute("loginUser", member);
      return "redirect:../post/list";
    }

  }

  @PostMapping("/{memberId}/follow")
  @ResponseBody
  public Map<String, Object> memberFollow(@PathVariable int memberId, HttpSession session)
      throws Exception {
    Map<String, Object> response = new HashMap<>();
    User loginUser = (User) session.getAttribute("loginUser");

    if (loginUser == null) {
      response.put("status", "notLoggedIn");
      return response;
    }

    int currentMemberId = loginUser.getId();
    boolean newIsFollowed = userService.memberFollow(currentMemberId, memberId);
    response.put("newIsFollowed", newIsFollowed);
    if (newIsFollowed) {
      User member = userService.get(memberId);
      if (member != null) {
        String content = loginUser.getNickName() + "님이 당신을 팔로우했습니다.";
        defaultNotificationService.send(content, member.getId());
      }
    }
    return response;
  }

  // 팔로우 상태 확인
  @PostMapping("/getFollowStatus")
  @ResponseBody
  public Map<Integer, Boolean> getFollowStatus(@RequestBody List<Integer> memberIds,
      HttpSession session)
      throws Exception {
    System.out.println("컨트롤러 팔로우상태확인 호출됨!");
    User loginUser = (User) session.getAttribute("loginUser");
    Map<Integer, Boolean> response = new HashMap<>();
    if (loginUser != null) {
      int currentMemberId = loginUser.getId();
      for (int memberId : memberIds) {
        boolean isFollowing = userService.isFollowed(currentMemberId, memberId);
        response.put(memberId, isFollowing);
      }
    }
    return response;
  }

  @GetMapping("/followers")
  public String followers(HttpSession session, Model model) throws Exception {
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/member/form";
    }
    List<User> followersList = userService.getFollowers(loginUser.getId());
    model.addAttribute("followersList", followersList);
    model.addAttribute("followerCount", followersList.size());
    return "member/followers";
  }

  @GetMapping("/followings")
  public String followings(HttpSession session, Model model) throws Exception {
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/member/form";
    }
    List<User> followingsList = userService.getFollowings(loginUser.getId());
    model.addAttribute("followingsList", followingsList);
    model.addAttribute("followingsCount", followingsList.size()); // 팔로잉 숫자 추가
    return "member/followings";
  }

  @GetMapping("/notifications/stream")
  public SseEmitter streamNotifications(HttpSession session) {
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
      throw new RuntimeException("로그인이 필요합니다.");
    }

    int memberId = loginUser.getId();
    return defaultNotificationService.connectNotification(memberId);
  }

  @PostMapping("/notifications/deleteAll")
  public ResponseEntity<?> deleteAllNotifications(HttpSession session) {
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
      return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
    }

    try {
      userService.deleteAllNotifications(loginUser.getId());
      return new ResponseEntity<>("모든 알림이 삭제되었습니다.", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/headerNotifications")
  public ResponseEntity<List<Notification>> getHeaderNotifications(HttpSession session) {
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    try {
      List<Notification> notifications = userService.getNotifications(loginUser.getId());
      return new ResponseEntity<>(notifications, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

