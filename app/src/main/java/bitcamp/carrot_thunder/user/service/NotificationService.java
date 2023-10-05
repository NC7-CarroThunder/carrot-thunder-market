package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.user.model.vo.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

  void send(String content, int receiverId);

  SseEmitter connectNotification(int memberId);

  void saveNotification(Notification notification);
}
