package bitcamp.carrot_thunder.user.service;


import bitcamp.carrot_thunder.NcpObjectStorageService;
import bitcamp.carrot_thunder.jwt.JwtUtil;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.dto.LoginRequestDto;
import bitcamp.carrot_thunder.user.dto.PasswdCheckRequestDto;
import bitcamp.carrot_thunder.user.dto.ProfileRequestDto;
import bitcamp.carrot_thunder.user.dto.ProfileResponseDto;
import bitcamp.carrot_thunder.user.dto.SignupRequestDto;
import bitcamp.carrot_thunder.user.model.dao.UserDao;
import bitcamp.carrot_thunder.user.model.vo.Notification;
import bitcamp.carrot_thunder.user.model.vo.Role;
import bitcamp.carrot_thunder.user.model.vo.User;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DefaultUserService implements UserService {

  private final UserDao userDao;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final NcpObjectStorageService ncpObjectStorageService;


  public DefaultUserService(UserDao userDao, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, NcpObjectStorageService ncpObjectStorageService) {
    this.userDao = userDao;
    this.jwtUtil = jwtUtil;
    this.passwordEncoder = passwordEncoder;
    this.ncpObjectStorageService = ncpObjectStorageService;
  }


  @Override
  public String login(LoginRequestDto loginInfo, HttpServletResponse response) throws Exception {
    User loginUser = this.get(loginInfo.getEmail());

    if (loginUser == null) {
      //model.addAttribute("refresh", "1;url=form");
      throw new Exception("등록된 사용자가 없습니다.");
      //return "redirect:/member/form";
    }

    if (!passwordEncoder.matches(loginInfo.getPassword(),loginUser.getPassword())) {
      throw new IllegalArgumentException("비밀 번호가 옳지 않습니다.");
    }

    response.addHeader(JwtUtil.AUTHORIZATION_HEADER,jwtUtil.createToken(loginUser.getNickName(),loginUser.getId()));

    if (loginUser.getRole() == Role.ADMIN) {
      System.out.println(loginUser.getRole());
      return "redirect:/admin/form";
    }

    return "redirect:/";

  }


  @Transactional
  @Override
  public void updatePasswordByName(String nickName, String password) throws Exception {
    userDao.updatePasswordByName(nickName, password);
  }

  @Transactional
  @Override
  public String signup(@Valid SignupRequestDto signupRequestDto, HttpServletResponse response) throws Exception{
    String email = signupRequestDto.getEmail();
    String password = passwordEncoder.encode(signupRequestDto.getPassword());
    String nickname = signupRequestDto.getNickname();
    String phone = signupRequestDto.getPhone();
    String address = signupRequestDto.getAddress();
    String detail_address = signupRequestDto.getDetailAddress();

    Optional<User> foundEmail = Optional.ofNullable(userDao.findByEmail(email));
    if (foundEmail.isPresent()) {
      throw new IllegalArgumentException("이미 사용중인 이메일 입니다.");
    }
    Optional<User> foundNickname = Optional.ofNullable(userDao.findByNickName(nickname));
    if (foundNickname.isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
    }

    User user = new User(email, password, nickname, phone, address, detail_address);
    userDao.signup(user);
    return "회원가입 완료";
  }
  @Transactional
  @Override
  public int add(User user) throws Exception {
    return userDao.insert(user);
  }

  @Override
  public List<User> list() throws Exception {
    return userDao.findAll();
  }

  @Override
  public User get(Long userId) throws Exception {
    return userDao.findBy(userId);
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
  public int update(User user) throws Exception {
    return userDao.update(user);
  }


  @Transactional
  @Override
  public int delete(Long userId) throws Exception {
    return userDao.delete(userId);
  }



  @Override
  public boolean memberFollow(Long currentMemberId, Long userId) throws Exception {
    boolean isFollowed = userDao.isFollowed(currentMemberId, userId);
    if (isFollowed) {
      userDao.deleteFollow(currentMemberId, userId);
    } else {
      userDao.insertFollow(currentMemberId, userId);
    }
    return !isFollowed;
  }

  @Override
  public boolean isFollowed(Long currentMemberId, Long userId) throws Exception {
    return userDao.isFollowed(currentMemberId, userId);
  }

  @Override
  public User get(Long userId, HttpSession session) throws Exception {
    User user = userDao.findBy(userId);
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser != null) {
//      int loggedInUserId = loginUser.getId();
      //member.setFollowed(userDao.isFollowed(loggedInUserId, userId));
    } else {
      //member.setFollowed(false);
    }
    return user;
  }

  @Override
  public List<User> getFollowers(Long userId) throws Exception {
    return userDao.getFollowers(userId);
  }

  @Override
  public List<User> getFollowings(Long userId) throws Exception {
    return userDao.getFollowings(userId);
  }

  @Override
  public List<Notification> getNotifications(Long userId) throws Exception {
    return userDao.findNotificationsByUserId(userId);
  }

  @Override
  public void deleteAllNotifications(Long userId) throws Exception {
    userDao.deleteAllNotifications(userId);
  }

  // 프로필 유저 정보 단순 조회
  @Override
  public ProfileResponseDto getProfile(Long userId) {
    User user = userDao.getProfile(userId);
    ProfileResponseDto dto = ProfileResponseDto.of(user);
    return dto;
  }

  // 프로필 유저 정보 세부 조회
  @Override
  public ProfileResponseDto getProfileDetail(UserDetailsImpl userDetails) {
    User user = userDao.getProfileDetail(userDetails.getUser().getId());
    ProfileResponseDto dto = ProfileResponseDto.detail(user);
    return dto;
  }

  // 프로필 유저 정보 업데이트
  @Override
  public ProfileRequestDto updateProfile(UserDetailsImpl userDetails, MultipartFile photo, ProfileRequestDto profileRequestDto) throws Exception {
    User user = userDetails.getUser();
    // 프로필 사진
    if (photo.getSize() > 0) {
      String uploadFileUrl = ncpObjectStorageService.uploadFile(
              "https://kr.object.ncloudstorage.com", "/carrot-thunder/user", photo);
      user.setPhoto(uploadFileUrl);

    } else {
      // 사용자가 사진을 업로드하지 않은 경우, 기존의 프로필 사진을 그대로 유지하도록 합니다.
      user.setPhoto(user.getPhoto());
    }

    user.setNickName(profileRequestDto.getNickName());
    user.setPhone(profileRequestDto.getPhone());
    user.setAddress(profileRequestDto.getAddress());
    user.setDetailAddress(profileRequestDto.getDetailAddress());
    user.setPassword(passwordEncoder.encode(profileRequestDto.getPassword()));

    return profileRequestDto;
  }

  // 프로필 유저 정보 수정 전 암호 체크
  @Override
  public String passwdCheck(UserDetailsImpl userDetails, PasswdCheckRequestDto passwdCheckRequestDto) throws Exception {
    String password = passwdCheckRequestDto.getPassword();
    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다!");
    }

    return "사용자 확인 완료";
  }

  // 잔액 조회
  @Override
  public String getBalance(UserDetailsImpl userDetails, HttpServletResponse response) {
    ProfileResponseDto dto = ProfileResponseDto.of(userDetails.getUser());
    return "잔액 조회 완료";
  }
}
