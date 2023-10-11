package bitcamp.carrot_thunder.findpw.model.dao;

import bitcamp.carrot_thunder.findpw.service.FindPwHashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FindPwDao {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private FindPwHashService findPwHashService;

  public void updatePassword(String email, String newPassword) {
    String hashedPassword = findPwHashService.hashPassword(newPassword);

    String sql = "UPDATE user SET password = ? WHERE email = ?";
    jdbcTemplate.update(sql, hashedPassword, email);
  }

  public boolean isEmailExists(String email) {
    String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, email);
    return count > 0;
  }

}
