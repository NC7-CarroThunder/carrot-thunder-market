package bitcamp.carrot_thunder.chatting.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatRoomVO implements Serializable {

  private static final long serialVersionUID = 1L;
  private String roomId;
  private Long postId;
  private int sellerId;
  private int buyerId;

  private int userId;
  private String lastMessage;
  private LocalDateTime lastUpdated;
  private String sellerNickname;
  private String buyerNickname;
  private String postTitle;
  private String postAttachment;
  private Long messageId;

  public String getRoomId() {
    return roomId;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  public Long getPostId() {
    return postId;
  }

  public void setPostId(Long postId) {
    this.postId = postId;
  }

  public int getSellerId() {
    return sellerId;
  }

  public void setSellerId(int sellerId) {
    this.sellerId = sellerId;
  }

  public int getBuyerId() {
    return buyerId;
  }

  public void setBuyerId(int buyerId) {
    this.buyerId = buyerId;
  }

  public String getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(String lastMessage) {
    this.lastMessage = lastMessage;
  }

  public LocalDateTime getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(LocalDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public String getSellerNickname() {
    return sellerNickname;
  }

  public void setSellerNickname(String sellerNickname) {
    this.sellerNickname = sellerNickname;
  }

  public String getBuyerNickname() {
    return buyerNickname;
  }

  public void setBuyerNickname(String buyerNickname) {
    this.buyerNickname = buyerNickname;
  }

  public String getPostTitle() {
    return postTitle;
  }

  public void setPostTitle(String postTitle) {
    this.postTitle = postTitle;
  }

  public String getPostAttachment() {
    return postAttachment;
  }

  public void setPostAttachment(String postAttachment) {
    this.postAttachment = postAttachment;
  }

  public Long getMessageId() {
    return messageId;
  }

  public void setMessageId(Long messageId) {
    this.messageId = messageId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }
}
