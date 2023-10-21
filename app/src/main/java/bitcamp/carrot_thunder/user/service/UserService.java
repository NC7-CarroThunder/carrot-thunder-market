package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.dto.LoginRequestDto;
import bitcamp.carrot_thunder.user.dto.PasswdCheckRequestDto;
import bitcamp.carrot_thunder.user.dto.PaymentsResponseDto;
import bitcamp.carrot_thunder.user.dto.PointRequestDto;
import bitcamp.carrot_thunder.user.dto.ProfileRequestDto;
import bitcamp.carrot_thunder.user.dto.ProfileResponseDto;
import bitcamp.carrot_thunder.user.dto.SignupRequestDto;
import bitcamp.carrot_thunder.user.dto.UserEmailCheckDto;
import bitcamp.carrot_thunder.user.dto.UserNameCheckDto;
import bitcamp.carrot_thunder.user.model.vo.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface UserService {

  String login(LoginRequestDto loginInfo, HttpServletResponse response) throws Exception;

  String signup(SignupRequestDto signupRequestDto, HttpServletResponse response) throws Exception;

  int add(User user) throws Exception;

  List<User> list() throws Exception;

  User get(Long userId) throws Exception;

  String UpdatePoint(PointRequestDto dto) throws Exception;

  User get(String email, String password) throws Exception;

  User get(String email) throws Exception;

  int update(User user) throws Exception;

  void updatePasswordByName(String nickName, String password) throws Exception;

//  boolean memberFollow(Long followerId, Long followingId) throws Exception;
//
//  boolean isFollowed(Long followerId, Long followingId) throws Exception;
//
//  List<User> getFollowers(Long userId) throws Exception;
//
//  List<User> getFollowings(Long userId) throws Exception;

  @Transactional
  int delete(Long userId) throws Exception;

  User get(Long userId, HttpSession session) throws Exception;

  ProfileResponseDto getProfile(Long id) throws Exception;

  ProfileResponseDto getProfileDetail(UserDetailsImpl userDetails) throws Exception;

  ProfileRequestDto updateProfile(UserDetailsImpl userDetails, MultipartFile multipartFile, ProfileRequestDto profileRequestDto, HttpServletResponse response) throws Exception;

  String passwdCheck(UserDetailsImpl userDetails, PasswdCheckRequestDto profileRequestDto) throws Exception;

  PaymentsResponseDto getBalance(UserDetailsImpl userDetails, HttpServletResponse response);

  Boolean userNameCheck(UserDetailsImpl userDetails, UserNameCheckDto userNameCheckDto, HttpServletResponse response) throws Exception;

  Boolean userEmailCheck(UserDetailsImpl userDetails, UserEmailCheckDto userEmailCheckDto, HttpServletResponse response) throws Exception;

}
