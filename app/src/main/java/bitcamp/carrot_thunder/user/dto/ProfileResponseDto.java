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

    public static ProfileResponseDto of(User user) {
        return ProfileResponseDto.builder()
                .nickname(user.getNickName())
                .photo(user.getPhoto())
                .build();
    }
}
