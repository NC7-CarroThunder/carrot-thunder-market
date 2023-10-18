package bitcamp.carrot_thunder.user.service;

import bitcamp.carrot_thunder.post.model.vo.ItemStatus;
import bitcamp.carrot_thunder.post.model.vo.Post;
import bitcamp.carrot_thunder.post.service.PostService;
import bitcamp.carrot_thunder.user.dto.PointRequestDto;
import bitcamp.carrot_thunder.user.model.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;


@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final UserService userService;

    private final PostService postService;

    public String UpdatePoint(PointRequestDto dto, HttpServletResponse response) throws Exception {
        try {
            User user = userService.get(Long.parseLong(dto.getUserId()));
            int updatePoint = user.getPoint() + Integer.parseInt(dto.getChargePoint());
            user.setPoint(updatePoint);
            userService.update(user);
            return String.valueOf(updatePoint);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw new Exception("포인트 추가 실패");
        }
    }

    public String Purchase(Long postId, User user, HttpServletResponse response) throws Exception {
        try {
            Post post = postService.get(postId);
            int updatePoint = user.getPoint() - post.getPrice();
            if (updatePoint < 0) {
                return "현재 갖고있는 포인트보다 물건의 가격이 더 비쌉니다.";
            }
            user.setPoint(updatePoint) ;
            post.setItemStatus(ItemStatus.ONGOING);
            post.setBuyerId(user.getId());
            postService.update(post);
            userService.update(user);
            return String.valueOf(updatePoint);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw new Exception("구매 실패");
        }

    }

    public String CancelPurchase(Long postId, User user, HttpServletResponse response) throws Exception {
        try {
            Post post = postService.get(postId);
            int updatePoint = user.getPoint() + post.getPrice();
            user.setPoint(updatePoint) ;
            post.setItemStatus(ItemStatus.SELLING);
            userService.update(user);
            postService.update(post);
            return String.valueOf(updatePoint);
        } catch( Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw new Exception("구매취소 실패");
        }

    }
    public String ConfirmedPurchase(Long postId, HttpServletResponse response) throws Exception {
        try {
            Post post = postService.get(postId);
            User user = userService.get(post.getUser().getId());
            user.setPoint(user.getPoint() + post.getPrice());
            userService.update(user);
            post.setItemStatus(ItemStatus.SOLDOUT);
            postService.update(post);
            //TODO : 해당 게시물 거래에대한 히스토리를 판매자와, 구매자에게 추가한다.
            return "성공";
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw new Exception("구매확정 실패");
        }

    }
}
