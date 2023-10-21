package bitcamp.carrot_thunder.chatting.service;

import bitcamp.carrot_thunder.chatting.model.vo.NotificationVO;
import java.util.List;

public interface NotificationService {

  int createNotification(NotificationVO notificationVO);

  int countUnreadNotifications(Long userId);

  List<NotificationVO> getAllNotifications(Long userId);

  int markAllNotificationsAsRead(Long userId);

  int deleteAllNotifications(Long userId);
}
