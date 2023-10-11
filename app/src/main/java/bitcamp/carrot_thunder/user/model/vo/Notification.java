package bitcamp.carrot_thunder.user.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

  private Long id;
  private Long userId;
  private String content;
  private LocalDateTime createdAt;
  private String type;
  private boolean isRead;


}
