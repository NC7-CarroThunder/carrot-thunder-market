package bitcamp.carrot_thunder.post.dto;

import bitcamp.carrot_thunder.post.model.vo.*;
import bitcamp.carrot_thunder.user.model.vo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.sql.Timestamp;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PostResponseDto {

  private User user;
  private Long postid;
  private String title;
  private String content;
  private int viewCount;
  private Timestamp createdAt;
  private ItemCategory itemCategory;
  private ItemStatus itemStatus;
  private DealingType dealingType;
  private String address;
  private int price;
  private String nickname;
  private int likeCount;
  private List<AttachedFile> attachedFilesPaths;
  private Boolean isLiked;

  public static PostResponseDto of(Post post) {
    return PostResponseDto.builder()
            .postid(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .price(post.getPrice())
            .nickname(post.getUser().getNickName())
            .isLiked(post.isLiked())
            .viewCount(post.getViewCount())
            .itemCategory(post.getItemCategory())
            .dealingType(post.getDealingType())
            .itemStatus(post.getItemStatus())
            .createdAt(post.getCreatedAt())
            .address(post.getAddress())
            .likeCount(post.getLikeCount())
            .attachedFilesPaths(post.getAttachedFiles())
            .build();
  }

}