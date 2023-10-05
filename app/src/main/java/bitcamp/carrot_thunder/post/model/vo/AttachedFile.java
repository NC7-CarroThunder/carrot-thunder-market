package bitcamp.carrot_thunder.post.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AttachedFile implements Serializable {

  private static final long serialVersionUID = 1L;

  int id;
  @Setter  String filePath;
  int postId;


}
