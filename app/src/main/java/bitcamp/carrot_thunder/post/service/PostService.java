package bitcamp.carrot_thunder.post.service;

import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Comment;
import bitcamp.carrot_thunder.post.model.vo.Post;
import java.util.List;
import javax.servlet.http.HttpSession;


public interface PostService {
    int add(Post post) throws Exception;
    int increaseViewCount(int postId) throws Exception;
    Post get(int id) throws Exception;
    int update(Post post) throws Exception;
    List<Post> list (HttpSession session) throws Exception;
    AttachedFile getAttachedFile(int fileId) throws Exception;
    int delete(int postId) throws Exception;
    int deleteAttachedFile(int fileId) throws Exception;

    boolean postLike(int postId, int memberId) throws Exception;
    List<Post> getLikedPosts(int memberId, HttpSession session) throws Exception;
    int getLikeCount(int postId) throws Exception;
    boolean isLiked(int postId, int memberId);

    List<Post> getBookmarkedPosts(int memberId, HttpSession session);
    boolean postBookmark(int postId, int memberId) throws Exception;
    Post setSessionStatus(int id, HttpSession session) throws Exception;
    boolean isBookmarked(int postId, int memberId);
    List<Post> getMyPosts(int memberId);

    int addComment(int postId, int memberId, String content);
    List<Comment> getCommentsByPostId(int postId);
    void deleteComment(int commentId, int memberId) throws Exception;
}
