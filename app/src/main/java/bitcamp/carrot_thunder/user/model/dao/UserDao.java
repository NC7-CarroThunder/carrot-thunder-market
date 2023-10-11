package bitcamp.carrot_thunder.user.model.dao;

import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.model.vo.Notification;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {

  int insert(User user);
  List<User> findAll();
  User findBy(Long userId);
  User findByName(String name);
  User findByEmailAndPassword(
      @Param("email") String email,
      @Param("password") String password);
  int update(User user);
  int delete(Long userId);

  void insertFollow(Long followerId, Long followingId);
  void deleteFollow(Long followerId, Long followingId);
  boolean isFollowed(Long followerId, Long followingId);
  List<User> getFollowers(Long userId);
  List<User> getFollowings(Long userId);

  int insertNotification(Notification notification);
  int updateReadStatus(int id, boolean isRead);
  List<Notification> findNotificationsByMemberId(Long userId);
  void deleteAllNotifications(Long userId) throws Exception;
}
