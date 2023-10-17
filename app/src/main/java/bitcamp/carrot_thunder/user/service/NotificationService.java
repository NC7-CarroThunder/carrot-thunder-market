package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.dto.NotificationResponseDto;
import bitcamp.carrot_thunder.user.model.vo.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {

  SseEmitter connectNotification(UserDetailsImpl userDetails) throws Exception;

  List<Notification> getNotifications(UserDetailsImpl userDetails) throws Exception;

  String deleteAllNotifications(UserDetailsImpl userDetails) throws Exception;

  NotificationResponseDto send(String content, Long receiverId) throws Exception;

  void saveNotification(Notification notification);



}
