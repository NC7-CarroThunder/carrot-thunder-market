package bitcamp.carrot_thunder.post.model.dao;

import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper

public interface PostDao {


    Long insert(Post post);
    Post findBy(Long id);
    Long updateCount(Long no);
    Long insertFiles(Post post);
    Long update(Post post);
    AttachedFile findFileByfileId(Long fileid);
    List<Post> findAll();
    Long delete(Long id);
    Long deleteFile(Long fileId);
    Long deleteFiles(Long postId);

    Long deleteLikes(Long postId);
    Long updateLikeCount(@Param("postId") Long postId, @Param("amount") int amount);
    Long insertLike(@Param("postId") Long postId, @Param("memberId") Long memberId);
    Long deleteLike(@Param("postId") Long postId, @Param("memberId") Long memberId);
    boolean isLiked(@Param("postId") Long postId, @Param("memberId") Long memberId);
    Long getLikeCount(Long postId);
    List<Post> getLikedPosts(Long memberId);
    List<Post> getMyPosts(Long memberId);

    Optional<Post> findPostDetailById(Long id);


    Optional<Object> findById(Long postId);

    List<AttachedFile> findImagesByPostId(Long postId);
}
