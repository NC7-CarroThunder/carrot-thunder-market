package bitcamp.carrot_thunder.chatting.model.dao;

import bitcamp.carrot_thunder.chatting.model.vo.NotificationVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationDAO {

  int insertNotification(NotificationVO notificationVO);

  int countUnreadNotifications(@Param("userId") Long userId);

  List<NotificationVO> selectAllNotifications(@Param("userId") Long userId);

  int markAllNotificationsAsRead(@Param("userId") Long userId);

  int deleteAllNotifications(@Param("userId") Long userId);

}
