package bitcamp.carrot_thunder.dto;

import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import lombok.Getter;

import java.util.List;

@Getter
public class PostRequestDto {

  private String title;
  private String content;
  private String itemCategory;
  private String dealingType;
  private String itemStatus;
  private String address;
  private int price;
  private List<AttachedFile> attachedFilesPaths;

//이걸로게시글작성
}