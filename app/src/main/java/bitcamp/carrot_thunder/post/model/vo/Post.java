package bitcamp.carrot_thunder.post.model.vo;

import bitcamp.carrot_thunder.post.dto.PostUpdateRequestDto;
import bitcamp.carrot_thunder.user.model.vo.User;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.mail.FetchProfile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long Id;

    private String title;
    private String content;
    private int viewCount;
    private Timestamp createdAt;
    private List<AttachedFile> attachedFiles;
    private User user;
    private ItemCategory itemCategory;
    private ItemStatus itemStatus;
    private DealingType dealingType;
    private String address;
    private int price;
    private Long buyerId;

    private int likeCount;
    private boolean liked;

    public void update(PostUpdateRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.price = requestDto.getPrice();
        this.address = requestDto.getAddress();
        this.itemCategory = ItemCategory.valueOf(requestDto.getItemCategory());
        this.dealingType = DealingType.valueOf(requestDto.getDealingType());
    }



}
