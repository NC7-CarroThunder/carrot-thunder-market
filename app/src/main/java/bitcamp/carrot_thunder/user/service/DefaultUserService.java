package bitcamp.carrot_thunder.user.service;


import bitcamp.carrot_thunder.NcpObjectStorageService;
import bitcamp.carrot_thunder.jwt.JwtUtil;
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
import bitcamp.carrot_thunder.user.model.dao.UserDao;
import bitcamp.carrot_thunder.user.model.vo.Role;
import bitcamp.carrot_thunder.user.model.vo.User;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

  private final UserDao userDao;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final NcpObjectStorageService ncpObjectStorageService;

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

    response.addHeader(JwtUtil.AUTHORIZATION_HEADER,jwtUtil.createToken(loginUser.getNickName(),loginUser.getId(),loginUser.getPoint(), loginUser.getPhoto()));

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


  @Override
  public String UpdatePoint(PointRequestDto dto) throws Exception {
    try {
      User user = this.get(Long.parseLong(dto.getUserId()));
      int updatePoint = user.getPoint() + Integer.parseInt(dto.getChargePoint());
      user.setPoint(updatePoint);
      this.update(user);
      return String.valueOf(updatePoint);
    } catch (Exception e) {
      throw new Exception("포인트 추가 실패");
    }
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



//  @Override
//  public boolean memberFollow(Long currentMemberId, Long userId) throws Exception {
//    boolean isFollowed = userDao.isFollowed(currentMemberId, userId);
//    if (isFollowed) {
//      userDao.deleteFollow(currentMemberId, userId);
//    } else {
//      userDao.insertFollow(currentMemberId, userId);
//    }
//    return !isFollowed;
//  }
//
//  @Override
//  public boolean isFollowed(Long currentMemberId, Long userId) throws Exception {
//    return userDao.isFollowed(currentMemberId, userId);
//  }

  @Override
  public User get(Long userId, HttpSession session) throws Exception {
    User user = userDao.findBy(userId);
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser != null) {
      //int loggedInUserId = loginUser.getId();
      //member.setFollowed(userDao.isFollowed(loggedInUserId, userId));
    } else {
      //member.setFollowed(false);
    }
    return user;
  }

//  @Override
//  public List<User> getFollowers(Long userId) throws Exception {
//    return userDao.getFollowers(userId);
//  }
//
//  @Override
//  public List<User> getFollowings(Long userId) throws Exception {
//    return userDao.getFollowings(userId);
//  }

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

  public ProfileRequestDto updateProfile(UserDetailsImpl userDetails, MultipartFile photo, ProfileRequestDto profileRequestDto, HttpServletResponse response) throws Exception {
    User user = userDetails.getUser();
    // 프로필 사진
    if (photo != null && photo.getSize() > 0) {
      String uploadFileUrl = ncpObjectStorageService.uploadFile(
              "carrot-thunder", "user/", photo);
      if (user.getPhoto() != null) {
        if (!user.getPhoto().isEmpty()) {
          ncpObjectStorageService.deleteFile("carrot-thunder", "user/" + user.getPhoto());
        }
      }


      user.setPhoto(uploadFileUrl);
    } else {
      // 사용자가 사진을 업로드하지 않은 경우, 기존의 프로필 사진을 그대로 유지하도록 합니다.
      user.setPhoto(user.getPhoto());
    }



    if (profileRequestDto.getNickname() != null) {
      user.setNickName(profileRequestDto.getNickname());
    }

    if (profileRequestDto.getPhone() != null) {
      user.setPhone(profileRequestDto.getPhone());
    }

    if (profileRequestDto.getAddress() != null) {
      user.setAddress(profileRequestDto.getAddress());
    }

    if (profileRequestDto.getDetailAddress() != null) {
      user.setDetailAddress(profileRequestDto.getDetailAddress());
    }

    if (profileRequestDto.getPassword() != null) {
      user.setPassword(passwordEncoder.encode(profileRequestDto.getPassword()));
    }
    userDao.updateProfile(user);
    response.addHeader(JwtUtil.AUTHORIZATION_HEADER,jwtUtil.createToken(user.getNickName(),user.getId(),user.getPoint(), user.getPhoto()));
    //닉네임 변경으로 인한 토큰 재발급
    return profileRequestDto;
  }

//  @Transactional
//  @Override
//  public int update(User user) throws Exception {
//    return userDao.update(user);
//  }

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
  public PaymentsResponseDto getBalance(UserDetailsImpl userDetails, HttpServletResponse response) {
    PaymentsResponseDto dto = PaymentsResponseDto.of(userDetails.getUser());
    return dto;
  }

  // 닉네임 중복 체크

  @Override
  public Boolean userNameCheck(UserDetailsImpl userDetails, UserNameCheckDto userNameCheckDto, HttpServletResponse response) {
    boolean isDuplicate = false;
    String nickname = userNameCheckDto.getNickname();
    long count = userDao.checkNicknameDuplicate(nickname);
    isDuplicate = count <= 0;

    return isDuplicate;
  }

  // 이메일 중복체크
  @Override
  public Boolean userEmailCheck(UserDetailsImpl userDetails, UserEmailCheckDto userEmailCheckDto, HttpServletResponse response) {
    boolean isDuplicate = false;
    String email = userEmailCheckDto.getEmail();
    long count = userDao.checkEmailDuplicate(email);
    isDuplicate = count <= 0;

    return isDuplicate;
  }
}
