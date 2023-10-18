package bitcamp.carrot_thunder.user.controller;

import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.dto.LoginRequestDto;
import bitcamp.carrot_thunder.user.dto.PointRequestDto;
import bitcamp.carrot_thunder.user.service.PurchaseService;
import bitcamp.carrot_thunder.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PayController {
    private final PurchaseService purchaseService;

    // 잔액 조회
//    @GetMapping("/payments")
//    @ResponseBody
//    public String getBalance(@AuthenticationPrincipal UserDetailsImpl userDetails,
//                        HttpServletResponse response) throws Exception {
//        userService.getBalance(userDetails,response);
//        return "성공";
//    }

    @PostMapping("/payments")
    @ResponseBody
    public String updatePoint(@RequestBody PointRequestDto dto, HttpServletResponse response) throws Exception {
        return purchaseService.UpdatePoint(dto,response);
    }
    //구매 api  요구 파라미터 : postId  리턴 - 성공 or 실패
    @PutMapping("/payments/purchase")
    @ResponseBody
    public String purchase(@AuthenticationPrincipal UserDetailsImpl userDetails,
                           @RequestBody Long postId, HttpServletResponse response) throws Exception {
        return purchaseService.Purchase(postId, userDetails.getUser(),response);
    }

    // 구매취소 api

    @PutMapping("/payments/cancel")
    @ResponseBody
    public String cancelPurchase(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                 @RequestBody Long postId, HttpServletResponse response) throws Exception {
        return purchaseService.CancelPurchase(postId,userDetails.getUser(),response);
    }

    // 구매 확정 api

    @PutMapping("/payments/ConfirmedPurchase")
    @ResponseBody
    public String confirmedPurchase(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @RequestBody Long postId, HttpServletResponse response) throws Exception {
        return purchaseService.ConfirmedPurchase(postId,response);
    }
}
