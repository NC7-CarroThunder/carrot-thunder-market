package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.user.model.vo.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

  SseEmitter connectNotification(Long userId);

  void sendNotification(String content, Long receiverId);

  void saveNotification(Notification notification);
}
