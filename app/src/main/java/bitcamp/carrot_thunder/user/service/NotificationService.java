package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.user.model.vo.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

  void send(String content, Long receiverId);

  SseEmitter connectNotification(Long userId);

  void saveNotification(Notification notification);
}
