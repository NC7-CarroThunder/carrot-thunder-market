package bitcamp.carrot_thunder.dto;

import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import lombok.Getter;

import java.util.List;

@Getter
public class PostUpdateRequestDto {
  private String title;
  private String content;
  private int price;
  private String address;
  private List<AttachedFile> attachedFilesPaths;

}
