package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.user.dto.NotificationResponseDto;
import bitcamp.carrot_thunder.user.model.dao.UserDao;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.model.vo.Notification;
import bitcamp.carrot_thunder.user.repository.EmitterRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class DefaultNotificationService implements NotificationService {

  private final static String ALARM_NAME = "alarm";
  private final EmitterRepository emitterRepository;
  private final UserDao userDao;
  private final SqlSession sqlSession;


  // 알림 조회
  @Override
  public SseEmitter connectNotification(Long userId) {
    SseEmitter emitter = new SseEmitter(60L * 1000 * 60);  // 1 hour
    emitterRepository.save(userId, emitter);
    emitter.onCompletion(() -> emitterRepository.delete(userId));
    emitter.onTimeout(() -> emitterRepository.delete(userId));
    return emitter;
  }

  // 알림 전송
  @Override
  public void sendNotification (String content, Long receiverId) {
    User user = userDao.findBy(receiverId);
    if (user == null) {
      throw new RuntimeException("Member not found with ID: " + receiverId);
    }

    Notification notification = createNotification(content, receiverId);
    // 알림 저장
    this.saveNotification(notification);
    // 알림을 전송하고 DTO로 변환하여 반환
    NotificationResponseDto notificationResponseDto = sendNotificationEvent(notification);
  }

  private Notification createNotification(String content, Long receiverId) {
    Notification notification = new Notification();
    notification.setUserId(receiverId);
    notification.setContent(content);
    notification.setType(ALARM_NAME);
    notification.setRead(false);
    notification.setCreatedAt(LocalDateTime.now());
    return notification;
  }

  public void saveNotification(Notification notification) {
    sqlSession.insert("bitcamp.carrot_thunder.user.model.dao.userDao.insertNotification", notification);
  }

  private NotificationResponseDto sendNotificationEvent(Notification notification) {
    SseEmitter emitter = emitterRepository.get(notification.getUserId());
    if (emitter != null) {
      try {
        emitter.send(SseEmitter.event()
                .id(String.valueOf(notification.getId()))
                .name(ALARM_NAME)
                .data(notification));
      } catch (IOException exception) {
        emitterRepository.delete(notification.getUserId());
        throw new RuntimeException("Error sending the notification.");
      }
    }

    return NotificationResponseDto.of(notification);
  }
}
