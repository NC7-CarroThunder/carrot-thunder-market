package bitcamp.carrot_thunder.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PointRequestDto {

    private String userId;
    private String chargePoint;
}
