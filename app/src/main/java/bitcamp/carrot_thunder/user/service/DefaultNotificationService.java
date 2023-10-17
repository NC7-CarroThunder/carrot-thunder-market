package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.dto.NotificationResponseDto;
import bitcamp.carrot_thunder.user.model.dao.NotificationDao;
import bitcamp.carrot_thunder.user.model.dao.UserDao;
import bitcamp.carrot_thunder.user.model.vo.Notification;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultNotificationService implements NotificationService {

  private final static String ALARM_NAME = "alarm";
  private final EmitterRepository emitterRepository;
  private final UserDao userDao;
  private final NotificationDao notificationDao;
  private final SqlSession sqlSession;

  @Override
  public SseEmitter connectNotification(UserDetailsImpl userDetails) throws Exception {
    if (userDetails == null || userDetails.getUser() == null) {
      throw new Exception("등록된 사용자가 없습니다.");
    }
    SseEmitter emitter = new SseEmitter(60L * 1000 * 60); // 1 hour
    emitterRepository.save(userDetails.getUser().getId(), emitter);
    emitter.onCompletion(() -> emitterRepository.delete(userDetails.getUser().getId()));
    emitter.onTimeout(() -> emitterRepository.delete(userDetails.getUser().getId()));
    return emitter;
  }

  @Override
  public NotificationResponseDto send(String content, Long receiverId) throws Exception {
    User user = userDao.findBy(receiverId);
    if (user == null) {
      throw new Exception("등록된 사용자가 없습니다.");
    }
    Notification notification = createNotification(content, receiverId);
    this.saveNotification(notification);
    sendNotificationEvent(notification);
    return NotificationResponseDto.of(notification);
  }

  @Override
  public List<Notification> getNotifications(UserDetailsImpl userDetails) {
    if (userDetails == null || userDetails.getUser() == null) {
      return Collections.emptyList(); // 사용자가 인증되지 않았을 때 빈 목록 반환
    }
    return notificationDao.findNotificationsByUserId(userDetails.getUser().getId());
  }

  @Override
  public String deleteAllNotifications(UserDetailsImpl userDetails) throws Exception{
    if (userDetails == null || userDetails.getUser() == null) {
      return "등록된 사용자가 없습니다."; // 사용자가 인증되지 않았을 때 아무 작업도 수행하지 않음
    }
    notificationDao.deleteAllNotifications(userDetails.getUser().getId());
    return "등록된 사용자가 없습니다.";
  }

  // 알림 생성
  private Notification createNotification(String content, Long receiverId) {
    Notification notification = new Notification();
    notification.setUserId(receiverId);
    notification.setContent(content);
    notification.setType(ALARM_NAME);
    notification.setRead(false);
    notification.setCreatedAt(LocalDateTime.now());
    return notification;
  }

  public void sendNotificationEvent(Notification notification) {
    SseEmitter emitter = emitterRepository.get(notification.getUserId());
    if (emitter != null) {
      try {
        emitter.send(SseEmitter.event()
                .id(String.valueOf(notification.getId()))
                .name(ALARM_NAME)
                .data(notification));
      } catch (IOException exception) {
        emitterRepository.delete(notification.getUserId());
        throw new RuntimeException("알림 전송 중 오류");
      }
    }
  }

  public void saveNotification(Notification notification) {
    sqlSession.insert("bitcamp.carrot_thunder.user.model.dao.NotificationDao.insertNotification", notification);
  }
}
