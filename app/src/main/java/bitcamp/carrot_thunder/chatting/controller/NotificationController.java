package bitcamp.carrot_thunder.chatting.controller;

import bitcamp.carrot_thunder.chatting.model.vo.NotificationVO;
import bitcamp.carrot_thunder.chatting.service.NotificationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @GetMapping("/notifications")
  public List<NotificationVO> getNotifications(@RequestParam Long userId) {
    return notificationService.getAllNotifications(userId);
  }

  @GetMapping("/countUnread")
  public int countUnreadNotifications(@RequestParam Long userId) {
    return notificationService.countUnreadNotifications(userId);
  }

  @PutMapping("/markAsRead")
  public int markAllNotificationsAsRead(@RequestParam Long userId) {
    return notificationService.markAllNotificationsAsRead(userId);
  }

  @DeleteMapping("/deleteAll")
  public int deleteAllNotifications(@RequestParam Long userId) {
    return notificationService.deleteAllNotifications(userId);
  }
}
