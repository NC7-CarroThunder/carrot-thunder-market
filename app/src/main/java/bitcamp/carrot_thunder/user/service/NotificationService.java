package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.user.model.vo.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

  void send(String content, Long receiverId);

  SseEmitter connectNotification(Long memberId);

  void saveNotification(Notification notification);
}
