package bitcamp.carrot_thunder.post.service;

import bitcamp.carrot_thunder.member.model.vo.Member;
import bitcamp.carrot_thunder.post.model.dao.PostDao;
import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class DefaultPostService implements PostService {

    PostDao postDao;

    public DefaultPostService(PostDao postDao) {
        this.postDao = postDao;
    }

    @Transactional

    public int add(Post post) throws Exception {
        int count = postDao.insert(post);
        if (post.getAttachedFiles().size() > 0) {
            postDao.insertFiles(post);
        }
        return count;
    }


    public Post get(int postId) throws Exception {
        return postDao.findBy(postId);
    }

    @Transactional
    public int update(Post post) throws Exception {
        int count = postDao.update(post);
        if (count > 0 && post.getAttachedFiles().size() > 0) {
            postDao.insertFiles(post);
        }
        return count;
    }


    @Transactional(readOnly = true)

    public List<Post> list(HttpSession session) throws Exception {
        Member loginUser = (Member) session.getAttribute("loginUser");
        List<Post> posts = postDao.findAll();
        if (loginUser != null) {
            int loggedInUserId = loginUser.getId();
            for (Post post : posts) {
                boolean isLiked = postDao.isLiked(post.getId(), loggedInUserId);
                post.setLiked(isLiked);
            }
        }
        return posts;
    }

    public AttachedFile getAttachedFile(int fileId) throws Exception {
        return postDao.findFileBy(fileId);
    }

    public int increaseViewCount(int boardNo) throws Exception {
        return postDao.updateCount(boardNo);
    }

    @Transactional
    public int delete(int postId) throws Exception {
        postDao.deleteFiles(postId);
        postDao.deleteLikes(postId);
        postDao.deleteBookmarks(postId);
        return postDao.delete(postId);
    }

    public int deleteAttachedFile(int fileId) throws Exception {
        return postDao.deleteFile(fileId);
    }

    /** 좋아요 개수 조회
     *
     *
     * @param postId
     * @return
     * @throws Exception ( 난중에 처리 )
     */

    public int getLikeCount(int postId) {
        return postDao.getLikeCount(postId);
    }

    public boolean postLike(int postId, int memberId) {
        boolean liked = postDao.isLiked(postId, memberId);
        if (liked) {
            postDao.deleteLike(postId, memberId);
            postDao.updateLikeCount(postId, -1);
        } else {
            postDao.insertLike(postId, memberId);
            postDao.updateLikeCount(postId, 1);
        }
        return !liked;
    }


    /** 관심버튼이 눌려진 게시글들의 정보를 가져오는 기능
     *
     *
     * @param memberId
     * @param session
     * @return
     */

    public List<Post> getLikedPosts(int memberId, HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        List<Post> posts = postDao.getLikedPosts(memberId);
        if (loginUser != null) {
            int loggedInUserId = loginUser.getId();
            for (Post post : posts) {
                boolean isLiked = postDao.isLiked(post.getId(), loggedInUserId);
                post.setLiked(isLiked);
            }
        }
        return posts;
    }

    /** 관심버튼 상태의 boolean
     *
     * enum도 괜찮긴할거같은데
     *
     * @param postId
     * @param memberId
     * @return
     */

    public boolean isLiked(int postId, int memberId) {
        return postDao.isLiked(postId, memberId);
    }



    /** 관심버튼을 눌렀을때 상태값을 변경해주는 기능
     *
     *
     * @param id
     * @param session
     * @return
     */
    public Post setSessionStatus(int id, HttpSession session) {
        Post post = postDao.findBy(id);
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser != null) {
            int loggedInUserId = loginUser.getId();
            boolean isLiked = postDao.isLiked(id, loggedInUserId);
            post.setLiked(isLiked);
        }
        return post;
    }

    /**
     * 게시글 상세 정보
     *
     * @param id
     * @return
     * @throws Exception ( 난중에 처리 )
     */
    @Override
    public Post getPostDetailById(int id) throws Exception {
        Optional<Post> post = postDao.
                findPostDetailById(id);
        if(post.isPresent()) {
            return post.get();
        }
        throw new Exception();
    }





    /**
     * 나의 게시글 조회 ( 굳이 여기에 있어야할까 )
     *
     *
     */
    @Transactional
  public List<Post> getMyPosts(int memberId) {
    return postDao.getMyPosts(memberId);
  }
}


