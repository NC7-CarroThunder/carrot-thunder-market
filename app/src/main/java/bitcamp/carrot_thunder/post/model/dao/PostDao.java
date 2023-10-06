package bitcamp.carrot_thunder.post.model.dao;

import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;
import java.util.List;
import java.util.Optional;

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
    List<Post> getMyPosts(int memberId);

    Optional<Post> findPostDetailById(int id);

}
