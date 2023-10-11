package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.dto.LoginRequestDto;
import bitcamp.carrot_thunder.user.dto.SignupRequestDto;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.model.vo.Notification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface UserService {

    public String patchPassword(UserDetailsImpl userDetails, String password) throws Exception;
    String login(LoginRequestDto loginInfo, HttpServletResponse response) throws Exception;

    String signup(SignupRequestDto signupRequestDto, HttpServletResponse response) throws Exception;
     int add(User user) throws Exception;
    List<User> list() throws Exception;
    User get(Long userId) throws Exception;
    User get(String email, String password) throws Exception;
    User get(String email) throws Exception;
    int update(User user) throws Exception;


    void updatePasswordByName(String nickName, String password) throws Exception;



    boolean memberFollow(Long followerId, Long followingId) throws Exception;
    boolean isFollowed(Long followerId, Long followingId) throws Exception;
    List<User> getFollowers(Long userId) throws Exception;
    List<User> getFollowings(Long userId) throws Exception;

    @Transactional
    int delete(Long userId) throws Exception;

    User get(Long userId, HttpSession session) throws  Exception;

    List<Notification> getNotifications(Long userId) throws Exception;
    void deleteAllNotifications(Long userId) throws Exception;
}
