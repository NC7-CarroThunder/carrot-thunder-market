package bitcamp.carrot_thunder.chatting.controller;

import bitcamp.carrot_thunder.chatting.model.vo.ChatMessageVO;
import bitcamp.carrot_thunder.chatting.model.vo.ChatRoomVO;
import bitcamp.carrot_thunder.chatting.service.ChattingService;
import bitcamp.carrot_thunder.user.model.vo.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChattingController {

  @Autowired
  private ChattingService chattingService;

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

  @GetMapping("/chatting/roomsForSeller")
  public List<ChatRoomVO> getRoomsForSeller(HttpServletRequest request) {
    User loginUser = (User) request.getSession().getAttribute("loginUser");
    if (loginUser == null) {
      throw new IllegalArgumentException("로그인이 필요합니다.");
    }
    return chattingService.getChatRoomsForSeller(Math.toIntExact(loginUser.getId()));
  }

  @GetMapping("/chatting/createOrGetChatRoom")
  public ResponseEntity<Map<String, Object>> createOrGetChatRoom(@RequestParam int sellerId,
      @RequestParam int currentUserId, @RequestParam int postId) {
    Map<String, Object> result = new HashMap<>();
    String existingRoomId = chattingService.checkChatRoomExists(sellerId, currentUserId, postId);
    if (existingRoomId != null) {
      ChatRoomVO existingRoom = chattingService.getChatRoomByPostIdAndUserId(postId, currentUserId);
      if (existingRoom != null && existingRoom.getRoomId().equals(existingRoomId)) {
        result.put("success", true);
        result.put("roomId", existingRoomId);
        return ResponseEntity.ok(result);
      }
    }
    try {

      String roomId = chattingService.createOrGetChatRoom(sellerId, currentUserId, postId);
      if (roomId == null) {
        throw new RuntimeException("채팅방 ID를 가져오는데 실패했습니다.");
      }
      result.put("success", true);
      result.put("roomId", roomId);
    } catch (Exception e) {
      result.put("success", false);
      result.put("message", e.getMessage());
    }
    return ResponseEntity.ok(result);
  }

  @GetMapping("/chatting/myChatRooms")
  public ResponseEntity<Map<String, Object>> getMyChatRooms(HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    User loginUser = (User) request.getSession().getAttribute("loginUser");
    if (loginUser == null) {
      throw new IllegalArgumentException("로그인이 필요합니다.");
    }
    response.put("chatRooms",
        chattingService.getChatRoomsForMember(Math.toIntExact(loginUser.getId())));
    response.put("loginUser", loginUser);
    return ResponseEntity.ok(response);
  }
}
