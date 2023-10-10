package bitcamp.carrot_thunder.dto;

import bitcamp.carrot_thunder.post.model.vo.DealingType;
import bitcamp.carrot_thunder.post.model.vo.ItemCategory;
import lombok.Getter;

@Getter
public class PostRequestDto {

    private String title;
    private String content;
    private int price;
    private DealingType dealingType;
    private ItemCategory itemCategory;
}
