package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.dto.LoginRequestDto;
import bitcamp.carrot_thunder.user.dto.SignupRequestDto;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.model.vo.Notification;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface UserService {

    public String patchPassword(UserDetailsImpl userDetails, String password) throws Exception;
    String login(LoginRequestDto loginInfo, HttpServletResponse response) throws Exception;

    String signup(SignupRequestDto signupRequestDto, HttpServletResponse response) throws Exception;
    // int add(User member) throws Exception;
    List<User> list() throws Exception;
    User get(Long memberId) throws Exception;
    User get(String email, String password) throws Exception;
    User get(String email) throws Exception;
    int update(User member) throws Exception;


    void updatePasswordByName(String nickName, String password) throws Exception;
    int delete(int memberId) throws Exception;


    boolean memberFollow(int followerId, int followingId) throws Exception;
    boolean isFollowed(int followerId, int followingId) throws Exception;
    List<User> getFollowers(Long memberId) throws Exception;
    List<User> getFollowings(Long memberId) throws Exception;
    User get(Long memberId, HttpSession session) throws  Exception;

    List<Notification> getNotifications(Long memberId) throws Exception;
    void deleteAllNotifications(Long memberId) throws Exception;
}
