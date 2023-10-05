package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.user.model.dao.UserDao;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.model.vo.Notification;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultMemberService implements UserService {

  UserDao memberDao;

  public DefaultMemberService(UserDao memberDao) {
    this.memberDao = memberDao;
  }

  @Transactional
  @Override
  public int add(User member) throws Exception {
    return memberDao.insert(member);
  }

  @Override
  public List<User> list() throws Exception {
    return memberDao.findAll();
  }

  @Override
  public User get(int memberId) throws Exception {
    return memberDao.findBy(memberId);
  }

  @Override
  public User get(String email, String password) throws Exception {
    return memberDao.findByEmailAndPassword(email, password);
  }

  @Transactional
  @Override
  public int update(User member) throws Exception {
    return memberDao.update(member);
  }

  @Transactional
  @Override
  public int delete(int memberId) throws Exception {
    return memberDao.delete(memberId);
  }


  @Override
  public boolean memberFollow(int currentMemberId, int memberId) throws Exception {
    boolean isFollowed = memberDao.isFollowed(currentMemberId, memberId);
    if (isFollowed) {
      memberDao.deleteFollow(currentMemberId, memberId);
    } else {
      memberDao.insertFollow(currentMemberId, memberId);
    }
    return !isFollowed;
  }

  @Override
  public boolean isFollowed(int currentMemberId, int memberId) throws Exception {
    return memberDao.isFollowed(currentMemberId, memberId);
  }

  @Override
  public User get(int memberId, HttpSession session) throws Exception {
    User member = memberDao.findBy(memberId);
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser != null) {
      int loggedInUserId = loginUser.getId();
      //member.setFollowed(memberDao.isFollowed(loggedInUserId, memberId));
    } else {
      //member.setFollowed(false);
    }
    return member;
  }

  @Override
  public List<User> getFollowers(int memberId) throws Exception {
    return memberDao.getFollowers(memberId);
  }

  @Override
  public List<User> getFollowings(int memberId) throws Exception {
    return memberDao.getFollowings(memberId);
  }

  @Override
  public List<Notification> getNotifications(int memberId) throws Exception {
    return memberDao.findNotificationsByMemberId(memberId);
  }

  @Override
  public void deleteAllNotifications(int memberId) throws Exception {
    memberDao.deleteAllNotifications(memberId);
  }
}
