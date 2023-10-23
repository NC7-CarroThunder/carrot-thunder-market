package bitcamp.carrot_thunder.chatting.controller;

import bitcamp.carrot_thunder.chatting.model.vo.NotificationVO;
import bitcamp.carrot_thunder.chatting.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWebSocketController {

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/notification/send")
  public void sendNotification(@Payload NotificationVO notificationVO) {
    notificationService.createNotification(notificationVO);
    messagingTemplate.convertAndSend("/topic/notifications/" + notificationVO.getUserId(),
        notificationVO);
  }
}
