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
    private String detail_address;

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
                .phone(user.getEmail())
                .address(user.getAddress())
                .detail_address(user.getDetailAddress())
                .build();
    }
}
