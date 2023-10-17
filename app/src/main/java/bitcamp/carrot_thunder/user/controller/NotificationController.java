package bitcamp.carrot_thunder.user.controller;

import bitcamp.carrot_thunder.dto.ResponseDto;
import bitcamp.carrot_thunder.post.dto.PostListResponseDto;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.dto.NotificationResponseDto;
import bitcamp.carrot_thunder.user.model.vo.Notification;
import bitcamp.carrot_thunder.user.service.NotificationService;
import bitcamp.carrot_thunder.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 가능한지 조회 후 생성
    @GetMapping("/notifications/check")
    @ResponseBody
    public ResponseDto<SseEmitter> streamNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception{
        return ResponseDto.success(notificationService.connectNotification(userDetails));
    }

    // 알림 가져오기
    @GetMapping("/notifications")
    public ResponseDto<List<Notification>> getHeaderNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception{
        return ResponseDto.success(notificationService.getNotifications(userDetails));
    }

    // 알림 삭제
    @DeleteMapping("/notifications")
    @ResponseBody
    public ResponseDto<String> deleteAllNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception{
        return ResponseDto.success(notificationService.deleteAllNotifications(userDetails));
    }

}
