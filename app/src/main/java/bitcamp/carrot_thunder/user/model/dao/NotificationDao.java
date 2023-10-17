package bitcamp.carrot_thunder.user.model.dao;

import bitcamp.carrot_thunder.user.model.vo.Notification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationDao {

    int insertNotification(Notification notification);

    int updateReadStatus(int id, boolean isRead);

    List<Notification> findNotificationsByUserId(Long userId);

    void deleteAllNotifications(Long userId) throws Exception;

}
