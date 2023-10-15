package bitcamp.carrot_thunder.post.dto;

import bitcamp.carrot_thunder.post.model.vo.*;
import bitcamp.carrot_thunder.user.model.vo.User;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PostListResponseDto {

  private Long postId;
  private String title;
  private int viewCount;
  private Timestamp createdAt;
  private ItemCategory itemCategory;
  private ItemStatus itemStatus;
  private DealingType dealingType;
  private int price;
  private String nickname;
  private int likeCount;
  private Boolean isLiked;
  private List<AttachedFile> attachedFilesPaths;

  public static PostListResponseDto of(Post post) {
    return PostListResponseDto.builder()
            .postId(post.getId())
            .title(post.getTitle())
            .price(post.getPrice())
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