package bitcamp.carrot_thunder.chatting.controller;

import bitcamp.carrot_thunder.chatting.model.vo.NotificationVO;
import bitcamp.carrot_thunder.chatting.service.NotificationService;
import java.util.List;

import bitcamp.carrot_thunder.jwt.JwtUtil;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @Autowired
  UserService userService;

  @Autowired
  JwtUtil jwtUtil;

  @GetMapping("/notifications")
  public List<NotificationVO> getNotifications(@RequestParam Long userId,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                                               HttpServletResponse servletResponse) {
    createToken(userDetails,servletResponse);
    return notificationService.getAllNotifications(userId);
  }

  @GetMapping("/countUnread")
  public int countUnreadNotifications(@RequestParam Long userId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                      HttpServletResponse servletResponse) {
    createToken(userDetails,servletResponse);
    return notificationService.countUnreadNotifications(userId);
  }

  @PutMapping("/markAsRead")
  public int markAllNotificationsAsRead(@RequestParam Long userId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                                        HttpServletResponse servletResponse) {
    createToken(userDetails,servletResponse);
    return notificationService.markAllNotificationsAsRead(userId);
  }

  @DeleteMapping("/deleteAll")
  public int deleteAllNotifications(@RequestParam Long userId,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    HttpServletResponse servletResponse) {
    createToken(userDetails,servletResponse);
    return notificationService.deleteAllNotifications(userId);
  }

  private void createToken(UserDetailsImpl userDetails, HttpServletResponse response) {
    if (userDetails != null) {
      User user = userDetails.getUser();
      response.addHeader(JwtUtil.AUTHORIZATION_HEADER,jwtUtil.createToken(user.getNickName(),user.getId(),user.getPoint(), user.getPhoto()));
    }

  }
}
