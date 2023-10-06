package bitcamp.carrot_thunder.user.model.dao;

import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.model.vo.Notification;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {

  int insert(User member);
  List<User> findAll();
  User findBy(int memberId);
  User findByName(String name);
  User findByEmail(String email);
  User findByEmailAndPassword(
      @Param("email") String email,
      @Param("password") String password);
  int update(User member);
  void updatePasswordByName(String nickName, String password);
  int delete(int no);

  void insertFollow(int followerId, int followingId);
  void deleteFollow(int followerId, int followingId);
  boolean isFollowed(int followerId, int followingId);
  List<User> getFollowers(int memberId);
  List<User> getFollowings(int memberId);

  int insertNotification(Notification notification);
  int updateReadStatus(int id, boolean isRead);
  List<Notification> findNotificationsByMemberId(int memberId);
  void deleteAllNotifications(int memberId) throws Exception;
}
