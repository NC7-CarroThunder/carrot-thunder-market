package bitcamp.carrot_thunder.user.service;

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

  @Autowired
  private SqlSession sqlSession;

  private final static String ALARM_NAME = "alarm";
  private final EmitterRepository emitterRepository;
  private final UserDao memberDao;

  public void send(String content, Long receiverId) {
    User user = memberDao.findBy(receiverId);
    if (user == null) {
      throw new RuntimeException("Member not found with ID: " + receiverId);
    }

    Notification notification = new Notification();
//    notification.setMemberId(receiverId);
    notification.setContent(content);
    notification.setType(ALARM_NAME);
    notification.setRead(false);
    notification.setCreatedAt(LocalDateTime.now());

    // 알림 저장
    this.saveNotification(notification);

    SseEmitter emitter = emitterRepository.get(receiverId);
    if (emitter != null) {
      try {
        emitter.send(SseEmitter.event()
            .id(String.valueOf(notification.getId()))
            .name(ALARM_NAME)
            .data(notification));
      } catch (IOException exception) {
        emitterRepository.delete(receiverId);
        throw new RuntimeException("Error sending the notification.");
      }
    }
  }

  public SseEmitter connectNotification(Long userId) {
    SseEmitter emitter = new SseEmitter(60L * 1000 * 60);  // 1 hour
    emitterRepository.save(userId, emitter);
    emitter.onCompletion(() -> emitterRepository.delete(userId));
    emitter.onTimeout(() -> emitterRepository.delete(userId));
    return emitter;
  }

  public void saveNotification(Notification notification) {
    sqlSession.insert("bitcamp.carrot_thunder.member.model.dao.MemberDao.insertNotification",
        notification);
  }
}
