package bitcamp.carrot_thunder.dto;

import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.DealingType;
import bitcamp.carrot_thunder.post.model.vo.ItemCategory;
import bitcamp.carrot_thunder.post.model.vo.ItemStatus;

import java.sql.Timestamp;
import java.util.List;

public class PostResponseDto {

    private Long postid;
    private String title;
    private String content;
    private int likeCount;
    private boolean liked;
    private int viewCount;
    private String address;
    private int price;
    private ItemCategory itemCategory;
    private ItemStatus itemStatus;
    private DealingType dealingType;
    private Timestamp createdAt;
    private List<AttachedFile> attachedFiles;


}
