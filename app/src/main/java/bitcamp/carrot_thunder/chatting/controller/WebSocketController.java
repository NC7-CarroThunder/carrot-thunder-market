package bitcamp.carrot_thunder.chatting.controller;

import bitcamp.carrot_thunder.chatting.model.vo.ChatMessageVO;
import bitcamp.carrot_thunder.chatting.model.vo.ChatRoomVO;
import bitcamp.carrot_thunder.chatting.model.vo.NotificationVO;
import bitcamp.carrot_thunder.chatting.service.ChattingService;
import bitcamp.carrot_thunder.chatting.service.DefaultNotificationService;
import bitcamp.carrot_thunder.chatting.service.PapagoTranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;

@RestController
public class WebSocketController {

  @Autowired
  private ChattingService chattingService;

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  @Autowired
  private PapagoTranslationService papagoTranslationService;

  @Autowired
  private DefaultNotificationService defaultNotificationService;

  @MessageMapping("/chat/sendMessage")
  @SendTo("/topic/public")
  public ChatMessageVO sendMessage(@Payload ChatMessageVO chatMessage,
      SimpMessageHeaderAccessor headerAccessor) {
    Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
    chatMessage.setSenderId(Math.toIntExact(userId));
    return chatMessage;
  }

  @MessageMapping("/send")
  public ChatMessageVO handleSendMessage(@Payload ChatMessageVO message,
      SimpMessageHeaderAccessor headerAccessor) {
    if (message.getRoomId() == null || message.getRoomId().trim().isEmpty()) {
      throw new IllegalArgumentException("채팅방 ID가 제공되지 않았습니다.");
    }

    Integer userId = message.getSenderId();

    if (userId == null) {
      throw new IllegalStateException("사용자가 로그인되지 않았습니다.");
    }

    message.setSenderId(Math.toIntExact(userId));
    ChatRoomVO chatRoom = chattingService.getChatRoomByRoomId(message.getRoomId());

    if (chatRoom == null) {
      throw new IllegalArgumentException("존재하지 않는 채팅방입니다. RoomId: " + message.getRoomId());
    }

    String senderNickname = chattingService.getNicknameByUserId(message.getSenderId());
    message.setSenderNickname(senderNickname);

    if (!message.getContent().startsWith(":emoji") || !message.getContent().endsWith(":")) {
      String originalMessage = message.getContent();
      String translatedMessage = papagoTranslationService.detectAndTranslate(originalMessage,
          message.getTargetLang());

      message.setContent(originalMessage); // 원본 메시지 설정
      message.setTransContent(translatedMessage); // 번역된 메시지 설정
      message.setSentAt(LocalDateTime.now());
    }

    Long receiverId;
    if (message.getSenderId() == chatRoom.getSellerId()) {
      receiverId = Long.valueOf(chatRoom.getBuyerId());
    } else {
      receiverId = Long.valueOf(chatRoom.getSellerId());
    }

    String previewMessage;
    if (message.getContent().length() > 15) {
      previewMessage = message.getContent().substring(0, 15) + "...";
    } else {
      previewMessage = message.getContent();
    }

    NotificationVO notification = new NotificationVO();
    notification.setUserId(receiverId); // 받는 사람의 ID를 설정
    notification.setContent(senderNickname + "님이 새로운 메시지를 보냈습니다.\n미리보기: " + previewMessage);
    notification.setType("CHAT");

    // 알림 저장 및 웹소켓으로 알림 전송
    defaultNotificationService.createNotification(notification);
    chattingService.updateChatRoomLastUpdated(message.getRoomId());
    ChatRoomVO anotherChatRoomId = chattingService.getAnotherChatRoom(chatRoom);
    chattingService.updateChatRoomLastUpdated(anotherChatRoomId.getRoomId());


    chattingService.saveMessage(message, chattingService.getAnotherChatRoom(chatRoom));

    // 현재 채팅방과 다른 채팅방 모두에 메시지를 전송
    messagingTemplate.convertAndSend("/topic/messages/" + message.getRoomId(), message);
    ChatRoomVO anotherChatRoom = chattingService.getAnotherChatRoom(chatRoom);
    messagingTemplate.convertAndSend("/topic/messages/" + anotherChatRoom.getRoomId(), message);
    messagingTemplate.convertAndSend("/topic/newChatRoom", message.getRoomId());

    return message;
  }
}
