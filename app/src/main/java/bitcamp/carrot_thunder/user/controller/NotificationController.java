package bitcamp.carrot_thunder.user.controller;

import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.model.vo.Notification;
import bitcamp.carrot_thunder.user.service.NotificationService;
import bitcamp.carrot_thunder.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;


@RestController
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    // 알림 조회
    @GetMapping("/notifications/check")
    @ResponseBody
    public SseEmitter streamNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        // 유효한 토큰인지 검사
//        if (userDetails != null) {
//            String username = userDetails.getUsername();
//            boolean isTokenValid = isTokenValid(username);
//            if (isTokenValid) {
//                // 토큰이 유효하면 SSE 이벤트 스트림을 생성하고 반환합니다.
//                return createSseEmitter();
//            }
//        }
        return notificationService.connectNotification(userDetails.getUser().getId());
    }
    @DeleteMapping("/notifications")
    @ResponseBody
    public ResponseEntity<?> deleteAllNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    // 유효한 토큰인지 검사
    if (userDetails == null) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    try {
      userService.deleteAllNotifications(userDetails.getUser().getId());
      return new ResponseEntity<>("모든 알림이 삭제되었습니다.", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @GetMapping("/notifications")
  public ResponseEntity<List<Notification>> getHeaderNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    if (userDetails == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    try {
      List<Notification> notifications = userService.getNotifications(userDetails.getUser().getId());
      return new ResponseEntity<>(notifications, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }



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
}
