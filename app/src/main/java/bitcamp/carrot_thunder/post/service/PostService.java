package bitcamp.carrot_thunder.post.service;

import bitcamp.carrot_thunder.post.model.dao.PostDao;
import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
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

    Post setSessionStatus(int id, HttpSession session) throws Exception;

    Post getPostDetailById(int id) throws Exception;

    List<Post> getMyPosts(int memberId);

    List<Post> searchPosts(String keyword);




}
