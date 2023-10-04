package bitcamp.carrot_thunder.post.model.vo;

import bitcamp.carrot_thunder.member.model.vo.Member;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class Post implements Serializable {

  private static final long serialVersionUID = 1L;

  private int id;

  private String title;
  private String content;
  private int viewCount;
  private Timestamp createdAt;
  private List<AttachedFile> attachedFiles;
  private int likeCount;
  private boolean liked;
  private Member member;
  private Item item;
  private ItemStatus itemStatus;
  private DealingType dealingType;
  private int price;
  private int starCount;




  public String toString() {
    return "Post{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", content='" + content + '\'' +
        ", member=" + member +
        ", viewCount=" + viewCount +
        ", createdAt=" + createdAt +
        ", attachedFiles=" + attachedFiles +
        ", item=" + item +
        ", itemStatus=" + itemStatus +
        ", dealingType=" + dealingType +
        ", price=" + price +
        ", starcount=" + starCount +
        '}';
  }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }


  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Post other = (Post) obj;
    return id == other.id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getViewCount() {
    return viewCount;
  }

  public void setViewCount(int viewCount) {
    this.viewCount = viewCount;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public List<AttachedFile> getAttachedFiles() {
    return attachedFiles;
  }

  public void setAttachedFiles(List<AttachedFile> attachedFiles) {
    this.attachedFiles = attachedFiles;
  }

  public int getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }

  public boolean isLiked() {
    return liked;
  }

  public void setLiked(boolean liked) {
    this.liked = liked;
  }

  public Member getMember() {
        return member;
    }



 public void setMember(Member member) {
        this.member = member;
    }


    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ItemStatus getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    public DealingType getDealingType() {
        return dealingType;
    }

    public void setDealingType(DealingType dealingType) {
        this.dealingType = dealingType;
    }
}
