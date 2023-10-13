package bitcamp.carrot_thunder.post.dto;

import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostRequestDto {

  private String title;
  private String content;
  private String itemCategory;
  private String dealingType;
  private String address;
  private int price;

//이걸로게시글작성
}