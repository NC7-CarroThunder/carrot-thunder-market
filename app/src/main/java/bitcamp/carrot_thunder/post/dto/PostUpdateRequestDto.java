package bitcamp.carrot_thunder.post.dto;

import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import lombok.Getter;


@Getter
public class PostUpdateRequestDto {
  private String title;
  private String content;
  private int price;
  private String address;
}
