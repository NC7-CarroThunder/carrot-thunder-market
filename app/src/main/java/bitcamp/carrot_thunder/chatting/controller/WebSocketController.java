package bitcamp.carrot_thunder.chatting.controller;

import bitcamp.carrot_thunder.chatting.model.vo.ChatMessageVO;
import bitcamp.carrot_thunder.chatting.model.vo.ChatRoomVO;
import bitcamp.carrot_thunder.chatting.service.ChattingService;
import bitcamp.carrot_thunder.chatting.service.PapagoTranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

  @Autowired
  private ChattingService chattingService;

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  @Autowired
  private PapagoTranslationService papagoTranslationService;

  @MessageMapping("/chat/sendMessage")
  @SendTo("/topic/public")
  public ChatMessageVO sendMessage(@Payload ChatMessageVO chatMessage,
      SimpMessageHeaderAccessor headerAccessor) {
    Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
    chatMessage.setSenderId(Math.toIntExact(userId));
    return chatMessage;
  }

  @MessageMapping("/send")
  public ChatMessageVO handleSendMessage(ChatMessageVO message,
      SimpMessageHeaderAccessor headerAccessor) {

    Integer userId = message.getSenderId();

    if (userId == null) {
      throw new IllegalStateException("사용자가 로그인되지 않았습니다.");
    }

    message.setSenderId(Math.toIntExact(userId));
    ChatRoomVO chatRoom = chattingService.getChatRoomByRoomId(message.getRoomId());

    if (chatRoom == null) {
      throw new IllegalArgumentException("존재하지 않는 채팅방입니다. RoomId: " + message.getRoomId());
    }

    String translatedMessage = papagoTranslationService.detectAndTranslate(message.getContent(),
        "ko");
    message.setContent(translatedMessage);

    chattingService.saveMessage(message);
    messagingTemplate.convertAndSend("/topic/messages/" + message.getRoomId(), message);

    return message;
  }
}
