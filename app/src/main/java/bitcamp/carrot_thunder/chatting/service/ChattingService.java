package bitcamp.carrot_thunder.chatting.service;

import bitcamp.carrot_thunder.chatting.model.vo.ChatMessageVO;
import bitcamp.carrot_thunder.chatting.model.vo.ChatRoomVO;
import java.util.List;

public interface ChattingService {

  ChatRoomVO getChatRoomByPostIdAndUserId(int postId, int currentUserId);

  List<ChatMessageVO> getMessagesByRoomId(String roomId);

  void saveMessage(ChatMessageVO message);

  List<ChatRoomVO> getChatRoomsForSeller(int sellerId);

  List<ChatRoomVO> getChatRoomsForMember(int memberId);

  String createOrGetChatRoom(int sellerId, int currentUserId, int postId);

  ChatRoomVO getChatRoomByRoomId(String roomId);

  String checkChatRoomExists(int sellerId, int currentUserId, int postId);

  String getNicknameByUserId(int userId);

  List<ChatRoomVO> getAllChatRoomsOrderedByLastUpdated();

  String getFirstAttachmentByPostId(Long postId);

  void updateChatRoomLastUpdated(String roomId);

  ChatMessageVO getChatMessageById(int messageId);

  void updateChatMessage(ChatMessageVO content);

  int leaveChatRoom(String roomId, int userId);

  void rejoinChatRoom(ChatRoomVO chatRoom);
}
