package bitcamp.carrot_thunder.post.dto;

import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.DealingType;
import bitcamp.carrot_thunder.post.model.vo.ItemCategory;
import bitcamp.carrot_thunder.post.model.vo.ItemStatus;
import bitcamp.carrot_thunder.post.model.vo.Post;
import bitcamp.carrot_thunder.user.model.vo.User;
import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PostListResponseDto {

    private User user;
    private Long postid;
    private String title;
    private int viewCount;
    private Timestamp createdAt;
    private ItemCategory itemCategory;
    private ItemStatus itemStatus;
    private DealingType dealingType;
    private int price;
    private String address;
    private String nickname;
    private int likeCount;
    private Boolean isLiked;
    private List<AttachedFile> attachedFilesPaths;

    public static PostListResponseDto of(Post post) {
        return PostListResponseDto.builder()
                .postid(post.getId())
                .title(post.getTitle())
                .price(post.getPrice())
                .address(post.getAddress())
                .nickname(post.getUser().getNickName())
                .isLiked(post.isLiked())
                .viewCount(post.getViewCount())
                .itemCategory(post.getItemCategory())
                .dealingType(post.getDealingType())
                .itemStatus(post.getItemStatus())
                .createdAt(post.getCreatedAt())
                .likeCount(post.getLikeCount())
                .attachedFilesPaths(post.getAttachedFiles())
                .build();
    }


    public void setIsLiked(boolean isLiked) {
    }
}