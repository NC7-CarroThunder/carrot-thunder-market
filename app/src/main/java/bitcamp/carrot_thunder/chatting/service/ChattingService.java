package bitcamp.carrot_thunder.chatting.service;

import bitcamp.carrot_thunder.chatting.model.vo.ChatMessageVO;
import bitcamp.carrot_thunder.chatting.model.vo.ChatRoomVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChattingService {

  ChatRoomVO getChatRoomByPostIdAndUserId(int postId, int currentUserId);

  List<ChatMessageVO> getMessagesByRoomId(String roomId);

  void saveMessage(ChatMessageVO message, ChatRoomVO anotherRoom);

  List<ChatRoomVO> getChatRoomsForSeller(int sellerId);

  List<ChatRoomVO> getChatRoomsForMember(long memberId);

  //String createOrGetChatRoom(int sellerId, int currentUserId, int postId);

  String createOrGetChatRoom(int sellerId, int currentUserId, int postId, boolean isSeller);

  ChatRoomVO getChatRoomByRoomId(String roomId);

  String checkChatRoomExists(int sellerId, int currentUserId, int postId, int userId);

  String getNicknameByUserId(int userId);

  List<ChatRoomVO> getAllChatRoomsOrderedByLastUpdated();

  String getFirstAttachmentByPostId(Long postId);

  void updateChatRoomLastUpdated(String roomId);

  ChatMessageVO getChatMessageById(int messageId);

  void updateChatMessage(ChatMessageVO content);

  int leaveChatRoom(String roomId, int userId);

  void rejoinChatRoom(ChatRoomVO chatRoom);

  ChatRoomVO getAnotherChatRoom(ChatRoomVO chatRoom);

  int deleteChatRoomByRoomId(@Param("roomId") String roomId, String nickName);
}
