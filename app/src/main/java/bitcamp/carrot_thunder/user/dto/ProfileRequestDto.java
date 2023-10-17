package bitcamp.carrot_thunder.user.dto;

import lombok.Getter;

@Getter
public class ProfileRequestDto {

    private String nickname;
    private String photo;
    private String phone;
    private String address;
    private String detailAddress;
    private String password;

}
