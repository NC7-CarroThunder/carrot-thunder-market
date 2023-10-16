package bitcamp.carrot_thunder.user.controller;

import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class FollowController {
//    @PostMapping("follow/{userId}/")
//    @ResponseBody
//    public Map<String, Object> memberFollow(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails)
//
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
//    }
//
//    //   팔로우 상태 확인
//    @PostMapping("/getFollowStatus")
//    @ResponseBody
//    public Map<Long, Boolean> getFollowStatus(@RequestBody List<Long> userIds,
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
//    }
//
//    @GetMapping("/followers")
//    public String followers(HttpSession session, Model model) throws Exception {
//    User loginUser = (User) session.getAttribute("loginUser");
//    if (loginUser == null) {
//      return "redirect:/member/form";
//    }
//    List<User> followersList = userService.getFollowers(loginUser.getId());
//    model.addAttribute("followersList", followersList);
//    model.addAttribute("followerCount", followersList.size());
//    return "member/followers";
//    }
//
//    @GetMapping("/followings")
//    public String followings(HttpSession session, Model model) throws Exception {
//    User loginUser = (User) session.getAttribute("loginUser");
//    if (loginUser == null) {
//      return "redirect:/member/form";
//    }
//    List<User> followingsList = userService.getFollowings(loginUser.getId());
//    model.addAttribute("followingsList", followingsList);
//    model.addAttribute("followingsCount", followingsList.size()); // 팔로잉 숫자 추가
//    return "member/followings";
//    }
}
