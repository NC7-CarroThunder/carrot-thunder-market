package bitcamp.carrot_thunder.post.model.vo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AttachedFile implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  @Setter private String filePath;
  private Long postId;

}
