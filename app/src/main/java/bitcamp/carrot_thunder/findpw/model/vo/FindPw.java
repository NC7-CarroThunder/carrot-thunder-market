package bitcamp.carrot_thunder.findpw.model.vo;

import java.io.Serializable;

public class FindPw implements Serializable {

  private static final long serialVersionUID = 1L;

  private int userId;
  private String username;
  private String email;

  public FindPw(int userId, String username, String email) {
    this.userId = userId;
    this.username = username;
    this.email = email;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String toString() {
    return "UserVO{" +
        "userId=" + userId +
        ", username='" + username + '\'' +
        ", email='" + email + '\'' +
        '}';
  }
}
