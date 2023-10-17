package bitcamp.carrot_thunder.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

}
