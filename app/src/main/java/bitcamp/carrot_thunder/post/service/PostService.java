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
    int add(Post post) throws Exception;

    int increaseViewCount(int postId) throws Exception;

    Post get(int id) throws Exception;

    List<Post> list(HttpSession session) throws Exception;

    AttachedFile getAttachedFile(int fileId) throws Exception;

    int deletePost(int postId,User user);


    boolean postLike(int postId, int memberId) throws Exception;

    List<Post> getLikedPosts(int memberId, HttpSession session) throws Exception;

    int getLikeCount(int postId) throws Exception;

    boolean isLiked(int postId, int memberId);

    Post setSessionStatus(int id, HttpSession session) throws Exception;


    List<Post> getMyPosts(int memberId);

    PostResponseDto getPost(int postId, UserDetailsImpl userDetails);

    List<Post> searchPosts(String keyword);

    Object updatePost(int postId, PostUpdateRequestDto requestDto, User user, List<MultipartFile> multipartFiles);
}