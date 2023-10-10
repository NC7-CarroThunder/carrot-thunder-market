package bitcamp.carrot_thunder.post.service;

import bitcamp.carrot_thunder.dto.PostResponseDto;
import bitcamp.carrot_thunder.dto.PostUpdateRequestDto;
import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.model.vo.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import javax.servlet.http.HttpSession;


public interface PostService {
    Long add(Post post) throws Exception;

    Long increaseViewCount(Long postId) throws Exception;

    Post get(Long id) throws Exception;

    List<Post> list(HttpSession session) throws Exception;

    AttachedFile getAttachedFile(Long fileId) throws Exception;

    Long deletePost(Long postId,User user);


    boolean postLike(Long postId, Long memberId) throws Exception;

    List<Post> getLikedPosts(Long memberId, HttpSession session) throws Exception;

    Long getLikeCount(Long postId) throws Exception;

    boolean isLiked(Long postId, Long memberId);

    Post setSessionStatus(Long id, HttpSession session) throws Exception;

    PostResponseDto getPost(Long postId, UserDetailsImpl userDetails);

    List<Post> searchPosts(String keyword);

    Object updatePost(Long postId, PostUpdateRequestDto requestDto, User user, List<MultipartFile> multipartFiles);
}