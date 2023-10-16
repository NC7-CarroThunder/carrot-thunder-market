package bitcamp.carrot_thunder.user.dto;

import bitcamp.carrot_thunder.user.model.vo.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class NotificationResponseDto {

    private Long id;
    private String content;
    private String type;
    private boolean read;
    private Timestamp createdAt;

    public static NotificationResponseDto of (Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .type(notification.getType())
                .read(notification.isRead())
                .createdAt(Timestamp.valueOf(notification.getCreatedAt()))
                .build();
    }
}
