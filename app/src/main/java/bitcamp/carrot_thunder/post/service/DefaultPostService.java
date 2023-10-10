package bitcamp.carrot_thunder.post.service;

import bitcamp.carrot_thunder.NcpObjectStorageService;
import bitcamp.carrot_thunder.config.NcpConfig;
import bitcamp.carrot_thunder.post.dto.PostResponseDto;
import bitcamp.carrot_thunder.post.dto.PostUpdateRequestDto;
import bitcamp.carrot_thunder.post.exception.NotFoundPostException;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.post.model.dao.PostDao;
import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class DefaultPostService implements PostService {


    @Autowired
    PostDao postDao;

    @Autowired


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
    public PostResponseDto updatePost(int postId, PostUpdateRequestDto postUpdateRequestDto, User user, List<String> remainingImages, List<MultipartFile> multipartFiles) {
        Post post = (Post) postDao.findById(postId).orElseThrow(() -> NotFoundPostException.EXCEPTION );

        if (user.getId() != post.getUser().getId()) {
            throw new IllegalArgumentException("유저 불일치");
        }

        if (post.getAttachedFiles().size() > 0) {
            postDao.insertFiles(post);
        }

        post.update(postUpdateRequestDto);

        handleImageUpdates(post, remainingImages);


        return PostResponseDto.of(post);
    }

    public List<String> handleImageUpdates(Post post, List<String> remainingImages) {
        List<AttachedFile> attachedFilesfiles = postDao.findImagesByPostId(post.getId());

        NcpConfig ncpConfig = new NcpConfig();
        NcpObjectStorageService ncpObjectStorageService = new NcpObjectStorageService(ncpConfig);
        for (AttachedFile attachedFile : attachedFilesfiles) {
            if (!remainingImages.contains(attachedFile.getFilename())) {
                // S3에서 이미지 삭제
                ncpObjectStorageService.deleteFile("bucket-name", "images/" + attachedFile.getFilename());
                // DB에서 이미지 삭제
                postDao.deleteFile(attachedFile.getId());
            }
        }
        return remainingImages;
    }




    @Transactional(readOnly = true)

    public List<Post> list(HttpSession session) throws Exception {
        User loginUser = (User) session.getAttribute("loginUser");
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


    /** 게시글 삭제
     *
     *
     * @param postId
     * @return
     * @throws Exception ( 난중에 처리 )
     */
    @Transactional
    public int delete(int postId) throws Exception {
        postDao.deleteFiles(postId);
        postDao.deleteLikes(postId);
        return postDao.delete(postId);
    }

    /** 게시글 상세에서 첨부파일 삭제
     *
     *
     * @param fileId
     * @return
     */

    public int deleteAttachedFile(int fileId)  {
        return postDao.deleteFile(fileId);
    }





    /** 관심버튼이 눌려진 게시글들의 정보를 가져오는 기능
     *
     * @param memberId
     * @param session
     * @return
     */

    public List<Post> getLikedPosts(int memberId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
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

    /** 관심 여부 확인 기능
     *
     * @param postId
     * @param memberId
     * @return
     */

    public boolean isLiked(int postId, int memberId) {
        return postDao.isLiked(postId, memberId);
    }

    /** 관심 개수 조회
     *
     * @param postId
     * @return
     */

    public int getLikeCount(int postId) {
        return postDao.getLikeCount(postId);
    }


    /** 유저가 관심을 눌렀을때, 관심 개수 변경
     *
     * @param postId
     * @param memberId
     * @return
     */
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

    /** 관심버튼을 눌렀을때 상태값을 변경해주는 기능
     *
     *
     * @param id
     * @param session
     * @return
     */
    public Post setSessionStatus(int id, HttpSession session) {
        Post post = postDao.findBy(id);
        User loginUser = (User) session.getAttribute("loginUser");
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
     * @throws Exception
     */
    @Override
    public Post getPostDetailById(int id) throws Exception {
        Optional<Post> post = postDao.
                findPostDetailById(id);
        if(post.isPresent()) {
            return post.get();
        }
        throw NotFoundPostException.EXCEPTION;
    }


    /**
     * 게시글 검색 기능
     *
     * @param keyword
     * @return
     */

    public List<Post> searchPosts(String keyword) {
        List<Post> posts = postDao.findAll();
        List<Post> searchResults = new ArrayList<>();

        for (Post post : posts) {
            if (post.getTitle().contains(keyword) || post.getContent().contains(keyword)) {
                searchResults.add(post);
            }
        }

        return searchResults;
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


