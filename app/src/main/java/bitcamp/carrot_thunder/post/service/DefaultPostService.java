package bitcamp.carrot_thunder.post.service;

import bitcamp.carrot_thunder.NcpObjectStorageService;
import bitcamp.carrot_thunder.config.NcpConfig;
import bitcamp.carrot_thunder.dto.PostListResponseDto;
import bitcamp.carrot_thunder.dto.PostResponseDto;
import bitcamp.carrot_thunder.dto.PostUpdateRequestDto;
import bitcamp.carrot_thunder.exception.NotHaveAuthorityException;
import bitcamp.carrot_thunder.post.exception.NotFoundPostException;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.post.model.dao.PostDao;
import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class DefaultPostService implements PostService {


    @Autowired
    PostDao postDao;


    @Transactional

    public Long add(Post post) throws Exception {
        Long count = postDao.insert(post);
        if (post.getAttachedFiles().size() > 0) {
            postDao.insertFiles(post);
        }
        return count;
    }


    public Post get(Long postId) throws Exception {
        return postDao.findBy(postId);
    }

        @Transactional
        public PostResponseDto updatePost(Long postId, PostUpdateRequestDto requestDto, User user, List<MultipartFile> multipartFiles) {
            Post post = (Post) postDao.findById(postId).orElseThrow(() -> NotFoundPostException.EXCEPTION );

            if (user.getId() != post.getUser().getId()) {
                throw NotHaveAuthorityException.EXCEPTION;
            }

            List<String> remainingImages = getRemainingImages(requestDto);

            if (!post.getAttachedFiles().isEmpty()) {
                postDao.insertFiles(post);
            }

            post.update(requestDto);

            handleImageUpdates(post, remainingImages);

            return PostResponseDto.of(post);
        }

        private List<String> getRemainingImages(PostUpdateRequestDto postUpdateRequestDto) {
            return postUpdateRequestDto.getAttachedFilesPaths().stream()
                    .map(AttachedFile::getFilename)
                    .collect(Collectors.toList());
        }

        private void handleImageUpdates(Post post, List<String> remainingImages) {
            List<AttachedFile> attachedFiles = postDao.findImagesByPostId(post.getId());

            NcpConfig ncpConfig = new NcpConfig();
            NcpObjectStorageService ncpObjectStorageService = new NcpObjectStorageService(ncpConfig);

            for (AttachedFile attachedFile : attachedFiles) {
                if (!remainingImages.contains(attachedFile.getFilename())) {
                    // S3에서 이미지 삭제
                    ncpObjectStorageService.deleteFile("https://kr.object.ncloudstorage.com", "/carrot-thunder/article" + attachedFile.getFilename());
                    // DB에서 이미지 삭제
                    postDao.deleteFile(attachedFile.getId());
                }
            }
        }




    @Transactional(readOnly = true)

    public List<Post> list(HttpSession session) throws Exception {
        User loginUser = (User) session.getAttribute("loginUser");
        List<Post> posts = postDao.findAll();
        if (loginUser != null) {
            Long loggedInUserId = loginUser.getId();
            for (Post post : posts) {
                boolean isLiked = postDao.isLiked(post.getId(), loggedInUserId);
                post.setLiked(isLiked);
            }
        }
        return posts;
    }

    public AttachedFile getAttachedFile(Long fileId) throws Exception {
        return postDao.findFileByfileId(fileId);
    }



    public Long increaseViewCount(Long boardNo) throws Exception {
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
    public Long deletePost(Long postId,User user) {
        Post post = (Post) postDao.findById(postId).orElseThrow(() -> NotFoundPostException.EXCEPTION);
        if (user.getId() != post.getUser().getId()) {

        }
        List<AttachedFile> attachedFiles = postDao.findImagesByPostId(post.getId());
        for (AttachedFile attachedFile : attachedFiles) {
            if (post.getAttachedFiles().size() > 0) {

                NcpConfig ncpConfig = new NcpConfig();
                NcpObjectStorageService ncpObjectStorageService = new NcpObjectStorageService(ncpConfig);
                ncpObjectStorageService.deleteFile("https://kr.object.ncloudstorage.com", "/carrot-thunder/article" + attachedFile.getFilename());
                // DB에서 이미지 삭제
                postDao.deleteFile(postId);
            }
        }


        postDao.deleteLikes(postId);
        return postDao.delete(postId);
    }











    /** 관심버튼이 눌려진 게시글들의 정보를 가져오는 기능
     *
     * @param memberId
     * @param session
     * @return
     */

    public List<Post> getLikedPosts(Long memberId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        List<Post> posts = postDao.getLikedPosts(memberId);
        if (loginUser != null) {
            Long loggedInUserId = loginUser.getId();
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

    public boolean isLiked(Long postId, Long memberId) {
        return postDao.isLiked(postId, memberId);
    }

    /** 관심 개수 조회
     *
     * @param postId
     * @return
     */

    public Long getLikeCount(Long postId) {
        return postDao.getLikeCount(postId);
    }


    /** 유저가 관심을 눌렀을때, 관심 개수 변경
     *
     * @param postId
     * @param memberId
     * @return
     */
    public boolean postLike(Long postId, Long memberId) {
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
    public Post setSessionStatus(Long id, HttpSession session) {
        Post post = postDao.findBy(id);
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            Long loggedInUserId = loginUser.getId();
            boolean isLiked = postDao.isLiked(id, loggedInUserId);
            post.setLiked(isLiked);
        }
        return post;
    }

    /**
     * 게시글 상세 정보
     *
     * @param postId
     * @return
     */
    public PostResponseDto getPost(Long postId, UserDetailsImpl userDetails)  {
        Post post = (Post) postDao.findById(postId).orElseThrow(() -> NotFoundPostException.EXCEPTION );
        return PostResponseDto.of(post);

    }


    /**
     * 게시글 검색 기능
     *
     * @param keyword
     * @return
     */

    public List<PostListResponseDto> searchPosts(String keyword) {
        List<PostListResponseDto> responseDto = postDao.findAll();
        List<PostListResponseDto> searchResults = new ArrayList<>();

        for (PostListResponseDto post : responseDto) {
            if (post.getTitle().contains(keyword) || post.getItemCategory().contains(keyword)) {
                searchResults.add(post);
            }
        }

        return searchResults;
    }

}


