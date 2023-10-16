package bitcamp.carrot_thunder.user.dto;

import bitcamp.carrot_thunder.user.model.vo.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentsResponseDto {
    private int point;

    public static PaymentsResponseDto of(User user) {
        return new PaymentsResponseDto(user.getPoint());
    }
}
