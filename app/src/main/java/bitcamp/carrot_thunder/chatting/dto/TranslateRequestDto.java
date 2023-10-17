package bitcamp.carrot_thunder.chatting.dto;

import lombok.Data;

@Data
public class TranslateRequestDto {

  private String message;
  private String targetLang;
}
