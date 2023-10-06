package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.jwt.JwtUtil;
import bitcamp.carrot_thunder.user.dto.LoginRequestDto;
import bitcamp.carrot_thunder.user.model.dao.UserDao;
import bitcamp.carrot_thunder.user.model.vo.Role;
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
  public String login(LoginRequestDto loginInfo, HttpServletResponse response) throws Exception {
    System.out.println(passwordEncoder.encode("1234"));
    User loginUser = this.get(loginInfo.getEmail());
    System.out.println(loginUser);

    if (loginUser == null) {
      //model.addAttribute("refresh", "1;url=form");
      throw new Exception("등록된 사용자가 없습니다.");
      //return "redirect:/member/form";
    }

    if (!passwordEncoder.matches(loginInfo.getPassword(),loginUser.getPassword())) {
      throw new IllegalArgumentException("비밀 번호가 옳지 않습니다.");
    }

    response.addHeader(JwtUtil.AUTHORIZATION_HEADER,jwtUtil.createToken(loginUser.getNickName()));

    if (loginUser.getRole() == Role.ADMIN) {
      System.out.println(loginUser.getRole());
      return "redirect:/admin/form";
    }

    return "redirect:/";


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
  public User get(int memberId) throws Exception {
    return userDao.findBy(memberId);
  }

  @Override
  public User get(String email, String password) throws Exception {
    return userDao.findByEmailAndPassword(email, password);
  }

  @Override
  public User get(String email) throws Exception {
    return userDao.findByEmail(email);
  }

  @Transactional
  @Override
  public int update(User member) throws Exception {
    return userDao.update(member);
  }

  @Transactional
  @Override
  public int delete(int memberId) throws Exception {
    return userDao.delete(memberId);
  }


  @Override
  public boolean memberFollow(int currentMemberId, int memberId) throws Exception {
    boolean isFollowed = userDao.isFollowed(currentMemberId, memberId);
    if (isFollowed) {
      userDao.deleteFollow(currentMemberId, memberId);
    } else {
      userDao.insertFollow(currentMemberId, memberId);
    }
    return !isFollowed;
  }

  @Override
  public boolean isFollowed(int currentMemberId, int memberId) throws Exception {
    return userDao.isFollowed(currentMemberId, memberId);
  }

  @Override
  public User get(int memberId, HttpSession session) throws Exception {
    User member = userDao.findBy(memberId);
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser != null) {
      int loggedInUserId = loginUser.getId();
      //member.setFollowed(userDao.isFollowed(loggedInUserId, memberId));
    } else {
      //member.setFollowed(false);
    }
    return member;
  }

  @Override
  public List<User> getFollowers(int memberId) throws Exception {
    return userDao.getFollowers(memberId);
  }

  @Override
  public List<User> getFollowings(int memberId) throws Exception {
    return userDao.getFollowings(memberId);
  }

  @Override
  public List<Notification> getNotifications(int memberId) throws Exception {
    return userDao.findNotificationsByMemberId(memberId);
  }

  @Override
  public void deleteAllNotifications(int memberId) throws Exception {
    userDao.deleteAllNotifications(memberId);
  }
}
