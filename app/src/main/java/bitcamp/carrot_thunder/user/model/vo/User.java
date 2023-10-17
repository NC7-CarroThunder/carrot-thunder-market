package bitcamp.carrot_thunder.user.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

  public static final long serialVersionUID = 1L;

  private Long id;
  private Role role;
  private Activation activation;
  private @Setter String email;
  private @Setter String password;
  private @Setter String nickName;
  private @Setter String phone;
  private @Setter String address;
  private @Setter String detailAddress;
  private Timestamp createdAt;
  private @Setter String photo;
  private  @Setter int point;

  public User(String email, String encodedPassword, String nickName, String phone,String address, String detailAddress) {
    this.email = email;
    this.password = encodedPassword;
    this.nickName = nickName;
    this.phone = phone;
    this.address = address;
    this.detailAddress = detailAddress;
  }

  public User(String email, String encodedPassword, String nickName, String phone,String address, String detailAddress, String photo) {
    this.email = email;
    this.password = encodedPassword;
    this.nickName = nickName;
    this.phone = phone;
    this.address = address;
    this.detailAddress = detailAddress;
    this.photo = photo;
  }

}
