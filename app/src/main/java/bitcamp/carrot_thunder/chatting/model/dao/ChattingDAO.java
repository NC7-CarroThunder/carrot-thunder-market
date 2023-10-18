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

    void saveMessage(ChatMessageVO message);

    List<ChatRoomVO> getChatRoomsForSeller(int sellerId);

    List<ChatRoomVO> getChatRoomsForMember(int memberId);

    String checkChatRoomExists(@Param("sellerId") int sellerId,
                               @Param("currentUserId") int currentUserId, @Param("postId") int postId);

    int createChatRoom(@Param("sellerId") int sellerId,
                       @Param("currentUserId") int currentUserId,
                       @Param("roomId") String roomId,
                       @Param("postId") int postId);



    ChatRoomVO getChatRoomByRoomId(String roomId);

    String getNicknameByUserId(int userId);

    List<ChatRoomVO> getAllChatRoomsOrderedByLastUpdated();

    String getFirstAttachmentByPostId(Long postId);

    int deleteChatRoomByPostId(@Param("postId") Long postId);

    int deleteChatMsgByRoomId(@Param("roomIds") String roomIdList);

    List<String> getRoomIdByPostId(@Param("postId") Long postId);

    void updateChatRoomLastUpdated(String roomId);
}
