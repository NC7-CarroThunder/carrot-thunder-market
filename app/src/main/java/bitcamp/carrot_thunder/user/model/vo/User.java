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

  private int id;
  private Role role;
  private Activation activation;
  private String email;
  private String password;
  private String nickName;
  private String phone;
  private String address;
  private String detailAddress;
  private Timestamp createdAt;
  private @Setter String photo;
  private int point;

}
