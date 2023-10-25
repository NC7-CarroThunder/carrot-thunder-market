package bitcamp.carrot_thunder.post.service;

import bitcamp.carrot_thunder.post.dto.PostListResponseDto;
import bitcamp.carrot_thunder.post.dto.PostRequestDto;
import bitcamp.carrot_thunder.post.dto.PostResponseDto;
import bitcamp.carrot_thunder.post.dto.PostUpdateRequestDto;
import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.model.vo.User;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;


public interface PostService {

    int add(Post post) throws Exception;

    PostResponseDto createPost(PostRequestDto postRequestDto, MultipartFile[] files,
                               UserDetailsImpl userDetails) throws Exception;

    int increaseViewCount(Long postId) ;

    Post get(Long id) throws Exception;

    List<PostListResponseDto> getPostlist(User user, int page, String category);
    List<PostListResponseDto> getPostlistByWord(User user, int page, String word);

    AttachedFile getAttachedFile(Long fileId) throws Exception;

    int deletePost(Long postId, User user);

    PostResponseDto getPost(Long postId, UserDetailsImpl userDetails);

    Object updatePost(Long postId, PostUpdateRequestDto requestDto, User user,MultipartFile[] multipartFiles) throws Exception;
    // Object updatePost(Long postId, PostUpdateRequestDto requestDto,UserDetailsImpl userDetails , MultipartFile[] files );

    int update(Post post);

    void toggleWishlist(Long article_id, User user);

    List<Post> getUserWishlist(User user);

    boolean isInWishlist(Long userId, Long postId);

    List<PostListResponseDto> getMyPosts(Long postId , UserDetailsImpl userDetails);
}
