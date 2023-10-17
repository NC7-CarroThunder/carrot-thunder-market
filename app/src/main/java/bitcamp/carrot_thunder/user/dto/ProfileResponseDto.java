package bitcamp.carrot_thunder.user.dto;

import bitcamp.carrot_thunder.user.model.vo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDto {

    private String nickname;
    private String photo;
    private String email;
    private String phone;
    private String address;
    private String detailAddress;

    public static ProfileResponseDto of(User user) {
        return ProfileResponseDto.builder()
                .nickname(user.getNickName())
                .photo(user.getPhoto())
                .build();
    }

    public static ProfileResponseDto detail(User user) {
        return ProfileResponseDto.builder()
                .nickname(user.getNickName())
                .photo(user.getPhoto())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .detailAddress(user.getDetailAddress())
                .build();
    }
}
