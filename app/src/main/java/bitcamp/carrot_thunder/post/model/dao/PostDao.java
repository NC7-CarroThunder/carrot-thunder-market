package bitcamp.carrot_thunder.post.model.dao;

import bitcamp.carrot_thunder.post.dto.PostListResponseDto;
import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.ItemCategory;
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
    List<Post> findByPageAndCategory(int start, int end, ItemCategory category);
    List<Post> findByPageAndWord(int start, int end, String word);
    int delete(Long id);
    int deleteFile(Long fileId);
    int deleteFiles(Long postId);

    int deleteChat(Long roodId);

    int deleteLikes(Long postId);
    int deleteLike(@Param("postId") Long postId, @Param("userId") Long userId);
    boolean isLiked(@Param("postId") Long postId, @Param("userId") Long userId);
    int getLikeCount(Long postId);
    Optional<Post> findPostDetailById(Long id);
    Optional<Object> findById(Long postId);
    List<AttachedFile> findImagesByPostId(Long postId);
    List<Post> findPostsByKeyword(String keyword, int offset, int size);

    int addWishlist(@Param("user_id") Long user_id, @Param("article_id") Long article_id);
    int removeWishlist(@Param("user_id") Long user_id, @Param("article_id") Long article_id);
    int isInWishlist(@Param("user_id") Long user_id, @Param("article_id") Long article_id);
    int deleteWishListByPostId(Long postId);
    List<Post> getPostsByUserId(Long userId);
    List<Post> getUserWishlist(Long user_id);
    Long getPostOwnerId(Long postId);

}
