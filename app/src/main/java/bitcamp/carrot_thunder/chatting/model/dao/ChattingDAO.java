package bitcamp.carrot_thunder.chatting.model.dao;

import bitcamp.carrot_thunder.chatting.model.vo.ChatMessageVO;
import bitcamp.carrot_thunder.chatting.model.vo.ChatRoomVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChattingDAO {

  ChatRoomVO getChatRoomByPostIdAndUserId(@Param("postId") int postId,
      @Param("currentUserId") int currentUserId);

  List<ChatMessageVO> getMessagesByRoomId(String roomId);

  void saveMessage(ChatMessageVO message, String sellerRoomId, String buyerRoomId);

  List<ChatRoomVO> getChatRoomsForSeller(long sellerId);

  List<ChatRoomVO> getChatRoomsForMember(long memberId);

  String checkChatRoomExists(@Param("sellerId") int sellerId,
      @Param("currentUserId") int currentUserId, @Param("postId") int postId, @Param("userId") int userId);

  int createChatRoom(@Param("sellerId") int sellerId,
      @Param("currentUserId") int currentUserId,
      @Param("roomId") String roomId,
      @Param("postId") int postId,
      @Param("userId") int userId);

  ChatRoomVO getChatRoomByRoomId(String roomId);

  String getNicknameByUserId(int userId);

  List<ChatRoomVO> getAllChatRoomsOrderedByLastUpdated();

  String getFirstAttachmentByPostId(Long postId);

  int deleteChatRoomByPostId(@Param("postId") Long postId);

  int deleteChatMsgByRoomId(@Param("roomIds") String roomId);

  List<String> getRoomIdByPostId(@Param("postId") Long postId);

  void updateChatRoomLastUpdated(@Param("roomId") String roomId);

  ChatMessageVO getChatMessageById(@Param("messageId") int messageId);

  void updateChatMessage(ChatMessageVO message);

  int leaveChatRoom(String roomId, int userId);

  void rejoinChatRoom(ChatRoomVO chatRoom);

  ChatRoomVO getAnotherChatRoom(long postId, long buyerId, long userId);

  int deleteChatRoomByRoomId(@Param("roomId") String roomId);
}
