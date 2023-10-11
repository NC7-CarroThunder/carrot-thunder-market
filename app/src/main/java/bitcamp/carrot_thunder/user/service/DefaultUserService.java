package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.jwt.JwtUtil;
import bitcamp.carrot_thunder.user.dto.LoginRequestDto;
import bitcamp.carrot_thunder.user.model.dao.UserDao;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.model.vo.Notification;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultUserService implements UserService {

  private final UserDao userDao;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public DefaultUserService(UserDao userDao, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
    this.userDao = userDao;
    this.jwtUtil = jwtUtil;
    this.passwordEncoder = passwordEncoder;
  }


  @Override
  public boolean login(LoginRequestDto loginInfo, HttpServletResponse response) throws Exception {

    User user = this.get(loginInfo.getEmail(),loginInfo.getPassword());
    if (user == null) {
      return false;
    }
    //TODO :
    //스프링시큐리티에로그인 알림
    //토큰값 갱신? 등 뭐 어쩌고저쩌고

    return true;
  }

  @Transactional
  @Override
  public int add(User member) throws Exception {
    return userDao.insert(member);
  }

  @Override
  public List<User> list() throws Exception {
    return userDao.findAll();
  }

  @Override
  public User get(Long memberId) throws Exception {
    return userDao.findBy(memberId);
  }

  @Override
  public User get(String email, String password) throws Exception {
    return userDao.findByEmailAndPassword(email, password);
  }

  @Transactional
  @Override
  public int update(User member) throws Exception {
    return userDao.update(member);
  }

  @Transactional
  @Override
  public int delete(Long userId) throws Exception {
    return userDao.delete(userId);
  }

    @Override
    public boolean memberFollow(int followerId, int followingId) throws Exception {
        return false;
    }

    @Override
    public boolean isFollowed(int followerId, int followingId) throws Exception {
        return false;
    }


 /* @Override
  public boolean memberFollow(Long currentMemberId, Long memberId) throws Exception {
    boolean isFollowed = userDao.isFollowed(currentMemberId, memberId);
    if (isFollowed) {
      userDao.deleteFollow(currentMemberId, memberId);
    } else {
      userDao.insertFollow(currentMemberId, memberId);
    }
    return !isFollowed;
  }

  @Override
  public boolean isFollowed(Long currentMemberId, Long memberId) throws Exception {
    return userDao.isFollowed(currentMemberId, memberId);
  }*/

  @Override
  public User get(Long memberId, HttpSession session) throws Exception {
    User member = userDao.findBy(memberId);
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser != null) {
//      int loggedInUserId = loginUser.getId();
      //member.setFollowed(userDao.isFollowed(loggedInUserId, memberId));
    } else {
      //member.setFollowed(false);
    }
    return member;
  }

  @Override
  public List<User> getFollowers(Long memberId) throws Exception {
    return userDao.getFollowers(memberId);
  }

  @Override
  public List<User> getFollowings(Long memberId) throws Exception {
    return userDao.getFollowings(memberId);
  }

  @Override
  public List<Notification> getNotifications(Long memberId) throws Exception {
    return userDao.findNotificationsByMemberId(memberId);
  }

  @Override
  public void deleteAllNotifications(Long memberId) throws Exception {
    userDao.deleteAllNotifications(memberId);
  }
}
