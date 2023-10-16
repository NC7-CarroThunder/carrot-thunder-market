package bitcamp.carrot_thunder.post.model.dao;

import bitcamp.carrot_thunder.post.dto.PostListResponseDto;
import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper

public interface PostDao {


    int insert(Post post);
    Post findBy(Long id);
    int updateCount(Long postId);
    int insertFiles(Post post);
    int update(Post post);
    AttachedFile findFileByfileId(Long fileId);
    List<Post> findAll();
    List<Post> findByPage(int start, int end);
    int delete(Long id);
    int deleteFile(Long fileId);

    int deleteChat(Long roodId);

    int deleteLikes(Long postId);
    int updateLikeCount(@Param("postId") Long postId, @Param("amount") int amount);
    int insertLike(@Param("postId") Long postId, @Param("userId") Long userId);
    int deleteLike(@Param("postId") Long postId, @Param("userId") Long userId);
    boolean isLiked(@Param("postId") Long postId, @Param("userId") Long userId);
    int getLikeCount(Long postId);
    Optional<Post> findPostDetailById(Long id);
    Optional<Post> findById(Long postId);
    List<AttachedFile> findImagesByPostId(Long postId);
    List<Post> findPostsByKeyword(String keyword, int offset, int size);
}