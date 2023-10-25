package bitcamp.carrot_thunder.chatting.controller;

import bitcamp.carrot_thunder.chatting.model.vo.ChatMessageVO;
import bitcamp.carrot_thunder.chatting.model.vo.ChatRoomVO;
import bitcamp.carrot_thunder.chatting.model.vo.NotificationVO;
import bitcamp.carrot_thunder.chatting.service.ChattingService;
import bitcamp.carrot_thunder.chatting.service.DefaultNotificationService;
import bitcamp.carrot_thunder.chatting.service.PapagoTranslationService;
import bitcamp.carrot_thunder.jwt.JwtUtil;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.model.vo.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChattingController {

  @Autowired
  private ChattingService chattingService;

  @Autowired
  private PapagoTranslationService translationService;

  @Autowired
  DefaultNotificationService defaultNotificationService;

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @Autowired
  JwtUtil jwtUtil;

//  @PostMapping("/translate")
//  public String translate(@RequestBody TranslateRequestDto request) {
//    String inputText = request.getMessage();
//    String targetLang = request.getTargetLang();
//    return translationService.detectAndTranslate(inputText, targetLang);
//  }

  @GetMapping("/chatting/room/{roomId}")
  public ResponseEntity<Map<String, Object>> getChatRoom(@PathVariable("roomId") String roomId,
      HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();

    if (roomId == null || roomId.trim().isEmpty() || !roomId.matches("^[a-fA-F0-9\\-]{36}$")) {
      throw new IllegalArgumentException("잘못된 채팅방 ID입니다.");
    }

    ChatRoomVO chatRoom = chattingService.getChatRoomByRoomId(roomId);
    if (chatRoom == null) {
      throw new IllegalArgumentException("채팅방을 찾을 수 없습니다.");
    }

    response.put("room", chatRoom);
    response.put("loginUser", request.getSession().getAttribute("loginUser"));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/chatting/message/{roomId}")
  public List<ChatMessageVO> getChatMessages(@PathVariable String roomId) {
    return chattingService.getMessagesByRoomId(roomId);
  }


  @GetMapping("/chatting/createOrGetChatRoom")
  public ResponseEntity<Map<String, Object>> createOrGetChatRoom(@RequestParam int sellerId,
      @RequestParam int currentUserId, @RequestParam int postId) {
    Map<String, Object> result = new HashMap<>();
    String existingRoomId = chattingService.checkChatRoomExists(sellerId, currentUserId, postId, currentUserId);
    if (existingRoomId != null) {

      ChatRoomVO existingRoom = chattingService.getChatRoomByPostIdAndUserId(postId, currentUserId);
      if (existingRoom != null && existingRoom.getRoomId().equals(existingRoomId)) {

        result.put("success", true);
        result.put("roomId", existingRoomId);
        return ResponseEntity.ok(result);
      }
    }
    try {
      String sellerRoomId = chattingService.createOrGetChatRoom(sellerId, currentUserId, postId, false);
      String BuyerRoomId = chattingService.createOrGetChatRoom(sellerId, currentUserId, postId, true);

      if (BuyerRoomId == null) {
        throw new RuntimeException("채팅방 ID를 가져오는데 실패했습니다.");
      }
      result.put("success", true);
      result.put("roomId", BuyerRoomId);
    } catch (Exception e) {
      result.put("success", false);
      result.put("message", e.getMessage());
    }
    return ResponseEntity.ok(result);
  }

  @GetMapping("/chatting/myChatRooms")
  public ResponseEntity<Map<String, Object>> getMyChatRooms(
          @AuthenticationPrincipal UserDetailsImpl userDetails,
          HttpServletResponse servletResponsere) {
    Map<String, Object> response = new HashMap<>();
    response.put("chatRooms", chattingService.getChatRoomsForMember(userDetails.getUser().getId()));
    User user = userDetails.getUser();
    servletResponsere.addHeader(JwtUtil.AUTHORIZATION_HEADER,jwtUtil.createToken(user.getNickName(),user.getId(),user.getPoint(), user.getPhoto()));
    return ResponseEntity.ok(response);
  }

//  @GetMapping("/chatting/allRoomsOrdered")
//  public ResponseEntity<List<ChatRoomVO>> getAllChatRoomsOrdered() {
//    List<ChatRoomVO> rooms = chattingService.getAllChatRoomsOrderedByLastUpdated();
//    return ResponseEntity.ok(rooms);
//  }

  @GetMapping("/chatting/getFirstAttachment")
  public ResponseEntity<String> getFirstAttachmentByPostId(@RequestParam Long postId) {
    String attachment = chattingService.getFirstAttachmentByPostId(postId);
    return ResponseEntity.ok(attachment);
  }


  @Transactional
  @PutMapping("/chatting/message/delete/{messageId}")
  public ResponseEntity<String> deleteChatMessage(@PathVariable int messageId,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  HttpServletResponse servletResponse) {
    ChatMessageVO message = chattingService.getChatMessageById(messageId);
    if (message != null) {
      message.setMessageId(messageId);
      message.setContent("삭제된 메시지입니다");
      message.setTransContent("");
      chattingService.updateChatMessage(message);
      User user = userDetails.getUser();
      servletResponse.addHeader(JwtUtil.AUTHORIZATION_HEADER,jwtUtil.createToken(user.getNickName(),user.getId(),user.getPoint(), user.getPhoto()));
      return ResponseEntity.ok("메시지가 삭제되었습니다.");
    } else {
      User user = userDetails.getUser();
      servletResponse.addHeader(JwtUtil.AUTHORIZATION_HEADER,jwtUtil.createToken(user.getNickName(),user.getId(),user.getPoint(), user.getPhoto()));
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("메시지를 찾을 수 없습니다.");
    }
  }

  @DeleteMapping("/chatting/delete/{roomId}")
  public ResponseEntity<String> deleteChatRoom(@PathVariable("roomId") String roomId,
      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                               HttpServletResponse servletResponse) {
    int rowsAffected = chattingService.deleteChatRoomByRoomId(roomId, userDetails.getUsername());
    User user = userDetails.getUser();
    servletResponse.addHeader(JwtUtil.AUTHORIZATION_HEADER,jwtUtil.createToken(user.getNickName(),user.getId(),user.getPoint(), user.getPhoto()));

    if (rowsAffected > 0) {
      // 채팅방 삭제에 성공한 경우
      return ResponseEntity.ok("채팅방이 삭제되었습니다.");
    } else {
      // 채팅방 삭제에 실패한 경우
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("채팅방 삭제에 실패했습니다.");
    }
  }

  // Handle MissingServletRequestParameterException and set a custom response status
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<String> handleMissingParam(MissingServletRequestParameterException ex) {
    String paramName = ex.getParameterName();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("Required request parameter '" + paramName + "' is not present");
  }
}
