package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.user.dto.LoginRequestDto;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.model.vo.Notification;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface UserService {

    boolean login(LoginRequestDto loginInfo, HttpServletResponse response) throws Exception;
    int add(User member) throws Exception;
    List<User> list() throws Exception;
    User get(int memberId) throws Exception;
    User get(String email, String password) throws Exception;
    int update(User member) throws Exception;
    int delete(int memberId) throws Exception;

    boolean memberFollow(int followerId, int followingId) throws Exception;
    boolean isFollowed(int followerId, int followingId) throws Exception;
    List<User> getFollowers(int memberId) throws Exception;
    List<User> getFollowings(int memberId) throws Exception;
    User get(int memberId, HttpSession session) throws  Exception;

    List<Notification> getNotifications(int memberId) throws Exception;
    void deleteAllNotifications(int memberId) throws Exception;
}