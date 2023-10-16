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
    PostResponseDto createPost(PostRequestDto postRequestDto, MultipartFile[] files,  UserDetailsImpl userDetails) throws Exception;
    int add(Post post) throws Exception;

    int increaseViewCount(Long postId) throws Exception;

    Post get(Long id) throws Exception;

    List<PostListResponseDto> getPostlist(User user, int page);

    AttachedFile getAttachedFile(Long fileId) throws Exception;

    String deletePost(Long postId, User user);

    PostResponseDto getPost(Long postId, UserDetailsImpl userDetails);

    List<PostListResponseDto> searchPosts(String keyword, UserDetailsImpl userDetails);

    Object updatePost(Long postId, PostUpdateRequestDto requestDto, User user, List<MultipartFile> multipartFiles);
}
