package bitcamp.carrot_thunder.post.model.dao;

import java.util.List;

import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostDao {
    int insert(Post post);
    Post findBy(int id);
    int updateCount(int no);
    int insertFiles(Post post);
    int update(Post post);
    AttachedFile findFileBy(int id);
    List<Post> findAll();
    int delete(int id);
    int deleteFile(int fileId);
    int deleteFiles(int postId);

    int deleteLikes(int postId);
    int updateLikeCount(@Param("postId") int postId, @Param("amount") int amount);
    int insertLike(@Param("postId") int postId, @Param("memberId") int memberId);
    int deleteLike(@Param("postId") int postId, @Param("memberId") int memberId);
    boolean isLiked(@Param("postId") int postId, @Param("memberId") int memberId);
    int getLikeCount(int postId);
    List<Post> getLikedPosts(int memberId);

    int deleteBookmarks(int postId);
    int insertBookmark(@Param("postId") int postId, @Param("memberId") int memberId);
    int deleteBookmark(@Param("postId") int postId, @Param("memberId") int memberId);
    boolean isBookmarked(@Param("postId") int postId, @Param("memberId") int memberId);
    List<Post> getBookmarkedPosts(int memberId);
    List<Post> getMyPosts(int memberId);

//    List<Comment> findCommentsByPostId(int postId);
//    void insertComment(int postId, int memberId, String content) throws Exception;
//    void deleteComment(@Param("commentId") int commentId, @Param("memberId") int memberId);
//    void insertComment(Comment comment);
}
