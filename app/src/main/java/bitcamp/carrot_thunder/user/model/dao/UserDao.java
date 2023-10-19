package bitcamp.carrot_thunder.user.model.dao;


import bitcamp.carrot_thunder.user.model.vo.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {

  int insert(User user);

  // 회원 가입
  int signup(User user);

  List<User> findAll();

  User findBy(Long userId);

  // 닉네임 중복 검사
  User findByNickName(String nickname);

  // 이메일 중복 검사
  User findByEmail(String email);

  User findByEmailAndPassword(
      @Param("email") String email,
      @Param("password") String password);

  int update(User user);

  void updatePasswordByName(String nickName, String password);

  int delete(Long userId);

  void insertFollow(Long followerId, Long followingId);

  void deleteFollow(Long followerId, Long followingId);

  boolean isFollowed(Long followerId, Long followingId);

  List<User> getFollowers(Long userId);

  List<User> getFollowings(Long userId);

//  int insertNotification(Notification notification);
//
//  int updateReadStatus(int id, boolean isRead);
//
//  List<Notification> findNotificationsByUserId(Long userId);
//
//  void deleteAllNotifications(Long userId) throws Exception;

  User getProfile(Long userId);

  User getProfileDetail(Long userId);

  long updateProfile(User user);

  long checkNicknameDuplicate(String nickname);

  long checkEmailDuplicate(String email);
}
