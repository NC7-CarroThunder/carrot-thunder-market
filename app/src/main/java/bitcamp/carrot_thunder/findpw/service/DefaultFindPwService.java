package bitcamp.carrot_thunder.findpw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class DefaultFindPwService {

  @Autowired
  JavaMailSender javaMailSender;

  public void sendTemporaryPasswordEmail(String toEmail, String temporaryPassword) {
    String subject = "[CarrtThunder] 임시 비밀번호 발송";
    String text = "[CarrtThunder] 임시 비밀번호 : " + temporaryPassword + "\n" + " 마이페이지에서 비밀번호를 변경해주세요.";

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toEmail);
    message.setSubject(subject);
    message.setText(text);

    javaMailSender.send(message);
  }
}
