package bitcamp.carrot_thunder.mail;

import bitcamp.carrot_thunder.user.model.vo.User;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

  @Autowired
  private JavaMailSender javaMailSender;

  @Autowired
  private TemplateEngine templateEngine;

  public void sendWelcomeEmail(User user) throws MessagingException {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

    // 이메일 내용을 Thymeleaf 템플릿으로 렌더링
    Context context = new Context();
    context.setVariable("member", user);
    String emailContent = templateEngine.process("mail/welcome", context);

    // 이메일 설정
    helper.setTo(user.getEmail());
    helper.setSubject("[CarroThunder] 회원가입이 완료되었습니다.");
    helper.setText(emailContent, true);

    // 이메일 전송
    javaMailSender.send(mimeMessage);
  }
}
