package bitcamp.carrot_thunder.dto;

import bitcamp.carrot_thunder.post.model.vo.*;
import bitcamp.carrot_thunder.user.model.vo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PostListResponseDto {

  private User user;
  private int postid;
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

  public static PostListResponseDto of(Post post) {
    return PostListResponseDto.builder()
            .postid(post.getId())
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
            .build();
  }

}