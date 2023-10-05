package bitcamp.carrot_thunder.post.service;

import bitcamp.carrot_thunder.user.model.vo.User;
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
  @Override
  public int add(Post post) throws Exception {
    int count = postDao.insert(post);
    if (post.getAttachedFiles().size() > 0) {
      postDao.insertFiles(post);
    }
    return count;
  }

  @Override
  public Post get(int postId) throws Exception {
    return postDao.findBy(postId);
  }

  @Transactional
  @Override
  public int update(Post post) throws Exception {
    int count = postDao.update(post);
    if (count > 0 && post.getAttachedFiles().size() > 0) {
      postDao.insertFiles(post);
    }
    return count;
  }

  @Override
  public List<Post> list(HttpSession session) throws Exception {
    User loginUser = (User) session.getAttribute("loginUser");
    List<Post> posts = postDao.findAll();
    if (loginUser != null) {
      int loggedInUserId = loginUser.getId();
      for (Post post : posts) {
        boolean isLiked = postDao.isLiked(post.getId(), loggedInUserId);
        boolean isBookmarked = postDao.isBookmarked(post.getId(), loggedInUserId);
        //post.setLiked(isLiked);
        //post.setBookmarked(isBookmarked);
      }
    }
    return posts;
  }

  @Override
  public AttachedFile getAttachedFile(int fileId) throws Exception {
    return postDao.findFileBy(fileId);
  }

  @Override
  public int increaseViewCount(int boardNo) throws Exception {
    return postDao.updateCount(boardNo);
  }

  @Transactional
  @Override
  public int delete(int postId) throws Exception {
    postDao.deleteFiles(postId);
    postDao.deleteLikes(postId);
    postDao.deleteBookmarks(postId);
    return postDao.delete(postId);
  }

  @Override
  public int deleteAttachedFile(int fileId) throws Exception {
    return postDao.deleteFile(fileId);
  }

  @Override
  public int getLikeCount(int postId) {
    return postDao.getLikeCount(postId);
  }

  @Override
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

  @Override
  public List<Post> getLikedPosts(int memberId, HttpSession session) {
    User loginUser = (User) session.getAttribute("loginUser");
    List<Post> posts = postDao.getLikedPosts(memberId);
    if (loginUser != null) {
      int loggedInUserId = loginUser.getId();
      for (Post post : posts) {
        boolean isLiked = postDao.isLiked(post.getId(), loggedInUserId);
        //post.setLiked(isLiked);
      }
    }
    return posts;
  }

  @Override
  public boolean postBookmark(int postId, int memberId) {
    boolean isBookmarked = postDao.isBookmarked(postId, memberId);
    if (isBookmarked) {
      postDao.deleteBookmark(postId, memberId);
    } else {
      postDao.insertBookmark(postId, memberId);
    }
    return !isBookmarked;
  }

  @Override
  public List<Post> getBookmarkedPosts(int memberId, HttpSession session) {
    User loginUser = (User) session.getAttribute("loginUser");
    List<Post> posts = postDao.getBookmarkedPosts(memberId);
    if (loginUser != null) {
      int loggedInUserId = loginUser.getId();
      for (Post post : posts) {
        boolean isBookmarked = postDao.isBookmarked(post.getId(), loggedInUserId);
        //post.setBookmarked(isBookmarked);
      }
    }
    return posts;
  }

  @Override
  public Post setSessionStatus(int id, HttpSession session) throws Exception {
    Post post = postDao.findBy(id);
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser != null) {
      int loggedInUserId = loginUser.getId();
      boolean isLiked = postDao.isLiked(id, loggedInUserId);
      boolean isBookmarked = postDao.isBookmarked(id, loggedInUserId);
      //post.setLiked(isLiked);
      //post.setBookmarked(isBookmarked);
    }
    return post;
  }

  @Override
  public boolean isLiked(int postId, int memberId) {
    return postDao.isLiked(postId, memberId);
  }

  @Override
  public boolean isBookmarked(int postId, int memberId) {
    return postDao.isBookmarked(postId, memberId);
  }

  @Transactional
  @Override
  public List<Post> getMyPosts(int memberId) {
    return postDao.getMyPosts(memberId);
  }

}
