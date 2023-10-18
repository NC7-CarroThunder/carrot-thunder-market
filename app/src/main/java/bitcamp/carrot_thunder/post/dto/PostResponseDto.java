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

public class PostResponseDto {

    private User user;
    private Long postId;
    private String title;
    private String content;
    private int viewCount;
    private Timestamp createdAt;
    private ItemCategory itemCategory;
    private ItemStatus itemStatus;
    private DealingType dealingType;
    private String address;
    private int price;
    private String nickName;
    private int likeCount;
    private List<AttachedFile> attachedFilesPaths;
    private Long buyerId;

    private Boolean isLiked;
    private Long userid;

    public static PostResponseDto of(Post post) {
        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .price(post.getPrice())
                .nickName(post.getUser().getNickName())
                .userid(post.getUser().getId())
                .isLiked(post.isLiked())
                .viewCount(post.getViewCount())
                .itemCategory(post.getItemCategory())
                .dealingType(post.getDealingType())
                .itemStatus(post.getItemStatus())
                .createdAt(post.getCreatedAt())
                .address(post.getAddress())
                .likeCount(post.getLikeCount())
                .attachedFilesPaths(post.getAttachedFiles())
                .buyerId(post.getBuyerId())
                .build();
    }

    public void setAttachedFiles(List<AttachedFile> attachedFilesPaths) {
        this.attachedFilesPaths = attachedFilesPaths;
    }
}