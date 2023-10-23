package bitcamp.carrot_thunder.chatting.service;

import bitcamp.carrot_thunder.chatting.model.dao.NotificationDAO;
import bitcamp.carrot_thunder.chatting.model.vo.NotificationVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class DefaultNotificationService implements NotificationService {

  @Autowired
  private NotificationDAO notificationDao;

  // SimpMessagingTemplate 객체를 주입받기 위한 코드
  @Autowired
  private SimpMessagingTemplate messagingTemplate;


  @Override
  public int countUnreadNotifications(Long userId) {
    return notificationDao.countUnreadNotifications(userId);
  }

  @Override
  public List<NotificationVO> getAllNotifications(Long userId) {
    return notificationDao.selectAllNotifications(userId);
  }

  @Override
  public int markAllNotificationsAsRead(Long userId) {
    return notificationDao.markAllNotificationsAsRead(userId);
  }

  @Override
  public int deleteAllNotifications(Long userId) {
    return notificationDao.deleteAllNotifications(userId);
  }

  @Override
  public int createNotification(NotificationVO notificationVO) {
    int result = notificationDao.insertNotification(notificationVO);
    if (result > 0) {
      messagingTemplate.convertAndSend("/topic/notifications/" + notificationVO.getUserId(),
          notificationVO);
    }
    return result;
  }
}
