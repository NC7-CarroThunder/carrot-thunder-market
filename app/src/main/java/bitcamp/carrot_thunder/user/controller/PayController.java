package bitcamp.carrot_thunder.user.controller;

import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.dto.LoginRequestDto;
import bitcamp.carrot_thunder.user.dto.PointRequestDto;
import bitcamp.carrot_thunder.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PayController {
    private final UserService userService;

    // 잔액 조회
    @GetMapping("/payments")
    @ResponseBody
    public String getBalance(@AuthenticationPrincipal UserDetailsImpl userDetails,
                        HttpServletResponse response) throws Exception {
        userService.getBalance(userDetails,response);
        return "성공";
    }

    @PostMapping("/payments")
    @ResponseBody
    public String updatePoint(@RequestBody PointRequestDto dto, HttpServletResponse response) throws Exception {
        return userService.UpdatePoint(dto);
    }

}
