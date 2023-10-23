package bitcamp.carrot_thunder.chatting.service;

import bitcamp.carrot_thunder.chatting.model.dao.ChattingDAO;
import bitcamp.carrot_thunder.chatting.model.vo.ChatMessageVO;
import bitcamp.carrot_thunder.chatting.model.vo.ChatRoomVO;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class DefaultChattingService implements ChattingService {

  @Autowired
  private ChattingDAO chattingDAO;

  @Autowired
  DefaultNotificationService defaultNotificationService;

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @Override
  public ChatRoomVO getChatRoomByPostIdAndUserId(int postId, int currentUserId) {
    return chattingDAO.getChatRoomByPostIdAndUserId(postId, currentUserId);
  }

  @Override
  public List<ChatMessageVO> getMessagesByRoomId(String roomId) {
    return chattingDAO.getMessagesByRoomId(roomId);
  }

  @Override
  public void saveMessage(ChatMessageVO message, ChatRoomVO anotherRoom) {
    String sellerRoomId;
    String buyerRoomId;
    if (anotherRoom.getUserId() == anotherRoom.getSellerId()) {
      sellerRoomId = message.getRoomId();
      buyerRoomId = anotherRoom.getRoomId();
    } else {
      buyerRoomId = anotherRoom.getRoomId();
      sellerRoomId = message.getRoomId();
    }
    chattingDAO.saveMessage(message, sellerRoomId, buyerRoomId);
  }

  @Override
  public List<ChatRoomVO> getChatRoomsForSeller(int sellerId) {
    return chattingDAO.getChatRoomsForSeller(sellerId);
  }

  @Override
  public List<ChatRoomVO> getChatRoomsForMember(int memberId) {
    return chattingDAO.getChatRoomsForMember(memberId);
  }


  @Override
  public String createOrGetChatRoom(int sellerId, int currentUserId, int postId, boolean isSeller) {
    String existingRoomId;
    if (isSeller) {
      existingRoomId = chattingDAO.checkChatRoomExists(sellerId, currentUserId, postId, sellerId);
    } else {
      existingRoomId = chattingDAO.checkChatRoomExists(sellerId, currentUserId, postId, currentUserId);
    }
    if (existingRoomId != null) {
      return existingRoomId;
    }
    String sellerNewRoomId = UUID.randomUUID().toString();
    String buyerNewRoomId = UUID.randomUUID().toString();
    if (isSeller) {
      createChatRoom(sellerId, currentUserId, sellerNewRoomId, postId, sellerId);
    } else {
      createChatRoom(sellerId, currentUserId, buyerNewRoomId, postId, currentUserId);
    }
    String newRoomId = isSeller ? sellerNewRoomId : buyerNewRoomId;
    return newRoomId;
  }

  @Override
  public ChatRoomVO getChatRoomByRoomId(String roomId) {
    return chattingDAO.getChatRoomByRoomId(roomId);
  }

  @Override
  public String checkChatRoomExists(int sellerId, int currentUserId, int postId, int userId) {
    return chattingDAO.checkChatRoomExists(sellerId, currentUserId, postId, userId);
  }

  @Override
  public String getNicknameByUserId(int userId) {
    return chattingDAO.getNicknameByUserId(userId);
  }

  @Override
  public List<ChatRoomVO> getAllChatRoomsOrderedByLastUpdated() {
    return chattingDAO.getAllChatRoomsOrderedByLastUpdated();
  }

  @Override
  public String getFirstAttachmentByPostId(Long postId) {
    return chattingDAO.getFirstAttachmentByPostId(postId);
  }

  @Override
  public void updateChatRoomLastUpdated(String roomId) {
    chattingDAO.updateChatRoomLastUpdated(roomId);
  }

  public int createChatRoom(int sellerId, int currentUserId, String newRoomId, int postId, int userId) {

    return chattingDAO.createChatRoom(sellerId, currentUserId, newRoomId, postId, userId);
  }


  @Override
  public ChatMessageVO getChatMessageById(int messageId) {
    return chattingDAO.getChatMessageById(messageId);
  }

  @Override
  public void updateChatMessage(ChatMessageVO message) {
    int messageId = message.getMessageId();
    ChatMessageVO existingMessage = chattingDAO.getChatMessageById(messageId);
    if (existingMessage != null) {
      existingMessage.setContent(message.getContent());
      existingMessage.setTransContent(message.getTransContent());
      chattingDAO.updateChatMessage(existingMessage);
    }
  }

  @Override
  public int leaveChatRoom(String roomId, int userId) {
    int rowsAffected = chattingDAO.leaveChatRoom(roomId, userId);
    return rowsAffected;
  }

  @Override
  public void rejoinChatRoom(ChatRoomVO chatRoom) {
    chattingDAO.rejoinChatRoom(chatRoom);
  }

  @Override
  public ChatRoomVO getAnotherChatRoom(ChatRoomVO chatRoom) {
    long userId;
    if (chatRoom.getUserId() == chatRoom.getSellerId()) {
      userId = chatRoom.getBuyerId();
    } else {
      userId = chatRoom.getSellerId();
    }
    return chattingDAO.getAnotherChatRoom(chatRoom.getPostId(), chatRoom.getBuyerId(), userId);
  }

  @Override
  public int deleteChatRoomByRoomId(String roomId, String nickName) {
    ChatRoomVO chatRoom = getChatRoomByRoomId(roomId);
    ChatMessageVO message = new ChatMessageVO();
    message.setRoomId(roomId);
    message.setContent("(" + nickName + ")님이 채팅방을 나갔습니다");
    message.setSenderId(chatRoom.getUserId());

    saveMessage(message, getAnotherChatRoom(chatRoom));
    int rowsAffected = chattingDAO.deleteChatRoomByRoomId(roomId);
    return rowsAffected;
  }
}
