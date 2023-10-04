package bitcamp.carrot_thunder.post.service;

import bitcamp.carrot_thunder.member.model.vo.Member;
import bitcamp.carrot_thunder.post.model.dao.PostDao;
import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultPostService implements PostService {

  PostDao postDao;

  public DefaultPostService(PostDao postDao) {
    this.postDao = postDao;
  }

  @Transactional

  public int add(Post post) throws Exception {
    int count = postDao.insert(post);
    if (post.getAttachedFiles().size() > 0) {
      postDao.insertFiles(post);
    }
    return count;
  }


  public Post get(int postId) throws Exception {
    return postDao.findBy(postId);
  }

  @Transactional
  public int update(Post post) throws Exception {
    int count = postDao.update(post);
    if (count > 0 && post.getAttachedFiles().size() > 0) {
      postDao.insertFiles(post);
    }
    return count;
  }


  @Transactional(readOnly = true)

  public List<Post> list(HttpSession session) throws Exception {
    Member loginUser = (Member) session.getAttribute("loginUser");
    List<Post> posts = postDao.findAll();
    if (loginUser != null) {
      int loggedInUserId = loginUser.getId();
      for (Post post : posts) {
        boolean isLiked = postDao.isLiked(post.getId(), loggedInUserId);
        post.setLiked(isLiked);
      }
    }
    return posts;
  }

  public AttachedFile getAttachedFile(int fileId) throws Exception {
    return postDao.findFileBy(fileId);
  }

  public int increaseViewCount(int boardNo) throws Exception {
    return postDao.updateCount(boardNo);
  }

  @Transactional
  public int delete(int postId) throws Exception {
    postDao.deleteFiles(postId);
    postDao.deleteLikes(postId);
    postDao.deleteBookmarks(postId);
    return postDao.delete(postId);
  }

  public int deleteAttachedFile(int fileId) throws Exception {
    return postDao.deleteFile(fileId);
  }


  public int getLikeCount(int postId) {
    return postDao.getLikeCount(postId);
  }

  public boolean postLike(int postId, int memberId) {
    boolean liked = postDao.isLiked(postId, memberId);
    if (liked) {
      postDao.deleteLike(postId, memberId);
      postDao.updateLikeCount(postId, -1);
    } else {
      postDao.insertLike(postId, memberId);
      postDao.updateLikeCount(postId, 1);
    }
    return !liked;
  }

  public List<Post> getLikedPosts(int memberId, HttpSession session) {
    Member loginUser = (Member) session.getAttribute("loginUser");
    List<Post> posts = postDao.getLikedPosts(memberId);
    if (loginUser != null) {
      int loggedInUserId = loginUser.getId();
      for (Post post : posts) {
        boolean isLiked = postDao.isLiked(post.getId(), loggedInUserId);
        post.setLiked(isLiked);
      }
    }
    return posts;
  }

  public boolean isLiked(int postId, int memberId) {
    return postDao.isLiked(postId, memberId);
  }


  //수정요구
    public Post setSessionStatus(int id, HttpSession session) throws Exception {
        return null;
    }


    @Transactional
  public List<Post> getMyPosts(int memberId) {
    return postDao.getMyPosts(memberId);
  }

}
