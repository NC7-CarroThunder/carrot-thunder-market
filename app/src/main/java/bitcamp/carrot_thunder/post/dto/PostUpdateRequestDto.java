package bitcamp.carrot_thunder.post.dto;

import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import lombok.Getter;

import java.util.List;


@Getter
public class PostUpdateRequestDto {
  private String title;
  private String content;
  private int price;
  private String address;
  private String dealingType;
  private String itemCategory;
  List<AttachedFile> attachedFiles;
}
