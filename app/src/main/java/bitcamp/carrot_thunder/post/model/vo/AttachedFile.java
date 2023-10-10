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

  private int id;
  @Setter private String filePath;
  private int postId;

  public String getFilename() {
    // 파일 경로에서 파일 이름만 추출하여 반환
    if (filePath != null && !filePath.isEmpty()) {
      int lastSlashIndex = filePath.lastIndexOf("/");
      if (lastSlashIndex != -1 && lastSlashIndex < filePath.length() - 1) {
        return filePath.substring(lastSlashIndex + 1);
      }
    }
    return null;
  }
}
