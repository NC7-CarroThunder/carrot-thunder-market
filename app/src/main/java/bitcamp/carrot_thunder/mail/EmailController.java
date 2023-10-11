package bitcamp.carrot_thunder.mail;

import bitcamp.carrot_thunder.user.model.vo.User;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class EmailController {

  @Autowired
  private EmailService emailService;

  @PostMapping("/email-welcome")
  public String sendWelcomeEmail(@RequestBody User user) throws MessagingException {
    emailService.sendWelcomeEmail(user);
    return "redirect:/";
  }
}
