package bitcamp.carrot_thunder.chatting.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationVO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long noti_id;
  private Long userId;
  private Long receiverId;
  private String content;
  private LocalDateTime created_at;
  private String type;
  private char isRead;
}
