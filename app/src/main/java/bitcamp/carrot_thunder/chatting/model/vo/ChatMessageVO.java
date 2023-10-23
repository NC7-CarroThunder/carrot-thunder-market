package bitcamp.carrot_thunder.chatting.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatMessageVO implements Serializable {

  private int messageId;
  private String roomId;
  private int senderId;
  private String content;
  private String transContent;
  private LocalDateTime sentAt;
  private char isRead;
  private String senderNickname;
  private String targetLang;


  public int getMessageId() {
    return messageId;
  }

  public void setMessageId(int messageId) {
    this.messageId = messageId;
  }

  public String getRoomId() {
    return roomId;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  public int getSenderId() {
    return senderId;
  }

  public void setSenderId(int senderId) {
    this.senderId = senderId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public LocalDateTime getSentAt() {
    return sentAt;
  }

  public void setSentAt(LocalDateTime sentAt) {
    this.sentAt = sentAt;
  }

  public char getIsRead() {
    return isRead;
  }

  public void setIsRead(char isRead) {
    this.isRead = isRead;
  }

  public String getSenderNickname() {
    return senderNickname;
  }

  public void setSenderNickname(String senderNickname) {
    this.senderNickname = senderNickname;
  }

  public String getTargetLang() {
    return targetLang;
  }

  public void setTargetLang(String targetLang) {
    this.targetLang = targetLang;
  }

  public String getTransContent() {
    return transContent;
  }

  public void setTransContent(String transContent) {
    this.transContent = transContent;
  }

}
