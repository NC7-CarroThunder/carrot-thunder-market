package bitcamp.carrot_thunder.post.service;

import bitcamp.carrot_thunder.NcpObjectStorageService;
import bitcamp.carrot_thunder.chatting.model.dao.ChattingDAO;
import bitcamp.carrot_thunder.chatting.model.vo.ChatRoomVO;
import bitcamp.carrot_thunder.chatting.model.vo.NotificationVO;
import bitcamp.carrot_thunder.chatting.service.DefaultNotificationService;
import bitcamp.carrot_thunder.exception.NotHaveAuthorityException;
import bitcamp.carrot_thunder.post.dto.PostListResponseDto;
import bitcamp.carrot_thunder.post.dto.PostRequestDto;
import bitcamp.carrot_thunder.post.dto.PostResponseDto;
import bitcamp.carrot_thunder.post.dto.PostUpdateRequestDto;
import bitcamp.carrot_thunder.post.exception.NotFoundPostException;
import bitcamp.carrot_thunder.post.model.dao.PostDao;
import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.DealingType;
import bitcamp.carrot_thunder.post.model.vo.ItemCategory;
import bitcamp.carrot_thunder.post.model.vo.ItemStatus;
import bitcamp.carrot_thunder.post.model.vo.Post;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.model.vo.User;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class DefaultPostService implements PostService {


    @Autowired
    ChattingDAO chattingDao;

    @Autowired
    PostDao postDao;

    @Autowired
    NcpObjectStorageService ncpObjectStorageService;

    @Autowired
    DefaultNotificationService defaultNotificationService;

    @Autowired
    SimpMessagingTemplate messagingTemplate;


    @Override
    public PostResponseDto createPost(PostRequestDto postRequestDto, MultipartFile[] files,
                                      UserDetailsImpl userDetails) throws Exception {
        Post post = new Post();
        post.setUser(userDetails.getUser());
        post.setAddress(postRequestDto.getAddress());
        post.setContent(postRequestDto.getContent());
        post.setTitle(postRequestDto.getTitle());
        post.setPrice(postRequestDto.getPrice());
        post.setItemCategory(ItemCategory.valueOf(postRequestDto.getItemCategory()));
        post.setDealingType(DealingType.valueOf(postRequestDto.getDealingType()));
        post.setItemStatus(ItemStatus.valueOf("SELLING"));

        ArrayList<AttachedFile> attachedFiles = new ArrayList<>();
        for (MultipartFile part : files) {
            if (part.getSize() > 0) {
                String uploadFileUrl = ncpObjectStorageService.uploadFile(
                        "carrot-thunder", "article/", part);
                AttachedFile attachedFile = new AttachedFile();
                attachedFile.setFilePath(uploadFileUrl);

                attachedFiles.add(attachedFile);
            }
        }
        post.setAttachedFiles(attachedFiles);
        this.add(post);
        return PostResponseDto.of(post);
    }

    @Transactional
    public int add(Post post) throws Exception {
        int count = postDao.insert(post);
        if (!post.getAttachedFiles().isEmpty()) {
            postDao.insertFiles(post);
        }
        return count;
    }


    public Post get(Long postId) throws Exception {
        return postDao.findBy(postId);
    }

    //TODO : 시간 여유가 있다면 , 파일첨부 + db와 S3에 삭제 기능
    //TODO : 로그인 유저 , 게시글 유저 비교 필요

    /**
     * 게시글 수정 기능
     *
     * @param postId
     * @param user
     * @param requestDto
     * @return
     */


    @Transactional
    public PostResponseDto updatePost(Long postId, PostUpdateRequestDto requestDto, User user, MultipartFile[] multipartFiles) throws Exception {

        Post post = (Post) postDao.findById(postId).orElseThrow(() -> NotFoundPostException.EXCEPTION);
        List<AttachedFile> fileList = postDao.findImagesByPostId(postId);
//        System.out.println("원래 파일 목록 ------------------------"  );
//        for (AttachedFile originElement : fileList) {
//            System.out.println(originElement.getFilePath());
//        }
//        System.out.println("업데이트할 기본 파일 목록 ------------------------"  );
//        for (AttachedFile element : requestDto.getAttachedFiles()) {
//            System.out.println(element.getFilePath());
//        }
//        if (multipartFiles != null) {
//            System.out.println("추가이미지 크기 : " + multipartFiles.length);
//        }
//        System.out.println("postId : " + postId);

        ArrayList<AttachedFile> attachedFiles = new ArrayList<>();
        //이미지 관련하여, 기존에 쓰이다가 버려질 이미지들 제거
        if (!requestDto.getAttachedFiles().isEmpty()) {
            for (AttachedFile originElement : fileList) {
                //여기서, 기존파일중, 업데이트과정중에서 제거한파일이 있으면 제거한 파일또한 스토리지에서 제거한다.d
                String path = originElement.getFilePath();
                boolean isExistFile = false;
                for (AttachedFile element : requestDto.getAttachedFiles()) {
                    //System.out.println("원래파일 : " + path + "  업데이트 요청 파일 : " + element.getFilePath() + "  " + element.getFilePath().equals(path));
                    if (element.getFilePath().equals(path)) {
                        isExistFile = true;
                    }
                }
                if (!isExistFile) {
                    ncpObjectStorageService.deleteFile("carrot-thunder", "article/" + path);
                    //System.out.println("삭제해야할 파일 : " + path);
                } else {
                    AttachedFile file = new AttachedFile();
                    file.setFilePath(path);
                    attachedFiles.add(file);
                }
            }
        } else {
            for (AttachedFile originElement : fileList) {
                String path = originElement.getFilePath();
                //System.out.println("삭제해야할 파일 : " + path);
                ncpObjectStorageService.deleteFile("carrot-thunder", "article/" + path);
            }
        }
        postDao.deleteFiles(postId);

        if (!Objects.equals(user.getNickName(), post.getUser().getNickName())) {
            throw NotHaveAuthorityException.EXCEPTION;
        }



        if (multipartFiles != null) {
            for (MultipartFile part : multipartFiles) {
                if (part.getSize() > 0) {
                    String uploadFileUrl = ncpObjectStorageService.uploadFile(
                            "carrot-thunder", "article/", part);
                    AttachedFile attachedFile = new AttachedFile();
                    attachedFile.setFilePath(uploadFileUrl);

                    attachedFiles.add(attachedFile);
                }
            }
        }

        post.setAttachedFiles(attachedFiles);
        postDao.insertFiles(post);
        post.update(requestDto);
        postDao.update(post);
        return PostResponseDto.of(post);
    }

    @Override
    public int update(Post post) {
        return postDao.update(post);
    }

//    private List<String> getRemainingImages(PostUpdateRequestDto postUpdateRequestDto) {
//        return postUpdateRequestDto.getAttachedFiles().stream()
//                .map(multipartFile -> {
//                    AttachedFile attachedFile = new AttachedFile();
//                    attachedFile.setFilePath(multipartFile.getOriginalFilename());
//                    return attachedFile.getFilename();
//                })
//                .collect(Collectors.toList());
//    }
//
//
//    private void handleImageUpdates(Post post, List<String> remainingImages) {
//        List<AttachedFile> attachedFiles = postDao.findImagesByPostId(post.getId());
//
//        NcpConfig ncpConfig = new NcpConfig();
//        NcpObjectStorageService ncpObjectStorageService = new NcpObjectStorageService(ncpConfig);
//
//        for (AttachedFile attachedFile : attachedFiles) {
//            if (!remainingImages.contains(attachedFile.getFilename())) {
//                // S3에서 이미지 삭제
//                ncpObjectStorageService.deleteFile("carrot-thunder", "article/" + attachedFile.getFilename());
//                // DB에서 이미지 삭제
//                postDao.deleteFile(attachedFile.getId());
//            }
//        }
//    }


    @Override
    public List<PostListResponseDto> getPostlist(User user, int page, String category) {
        List<Post> posts;
        if (category.equals("TOTAL")) {
            posts = postDao.findByPage((page - 1) * 8, 8);
        } else {
            posts = postDao.findByPageAndCategory((page - 1) * 8, 8, ItemCategory.valueOf(category));
        }
        List<PostListResponseDto> dtoList = new ArrayList<>();

        for (Post post : posts) {
            PostListResponseDto responseDto = PostListResponseDto.of(post);
            boolean isLiked = false;
//            if (user != null) {
//                isLiked = postDao.isLiked(post.getId(), user.getId());
//            }
            responseDto.setIsLiked(isLiked);
            dtoList.add(responseDto);
        }

        return dtoList;
    }

    @Override
    public List<PostListResponseDto> getPostlistByWord(User user, int page, String word) {
        List<Post> posts;
        posts = postDao.findByPageAndWord((page - 1) * 8, 8, word);
        List<PostListResponseDto> dtoList = new ArrayList<>();

        for (Post post : posts) {
            PostListResponseDto responseDto = PostListResponseDto.of(post);
            boolean isLiked = false;
            responseDto.setIsLiked(isLiked);
            dtoList.add(responseDto);
        }

        return dtoList;
    }

    public AttachedFile getAttachedFile(Long fileId) throws Exception {
        return postDao.findFileByfileId(fileId);
    }


    //TODO : 로그인 유저 , 게시글 유저 비교 필요

    /**
     * 게시글 삭제
     *
     * @param user
     * @param postId
     * @return
     * @throws Exception ( 난중에 처리 )
     */
    @Transactional
    public int deletePost(Long postId, User user) {
        Post post = (Post) postDao.findById(postId).orElseThrow(() -> NotFoundPostException.EXCEPTION);
        if (!Objects.equals(user.getNickName(), post.getUser().getNickName())) {
            throw NotHaveAuthorityException.EXCEPTION;
        }
        List<AttachedFile> attachedFiles = postDao.findImagesByPostId(post.getId());
        for (AttachedFile attachedFile : attachedFiles) {
            if (!post.getAttachedFiles().isEmpty()) {
                // S3에서 이미지 삭제
                ncpObjectStorageService.deleteFile("carrot-thunder",
                        "article/" + attachedFile.getFilePath());
                // DB에서 이미지 삭제
                postDao.deleteFile(attachedFile.getId());
            }
        }
//        List<String> roomIdList = chattingDao.getRoomIdByPostId(postId);

        chattingDao.deleteChatRoomByPostId(postId);
        postDao.deleteWishListByPostId(postId);

        return postDao.delete(postId);

    }


    /**
     * 게시글 상세 정보 기능
     *
     * @param postId
     * @param userDetails
     * @return
     */

    @Override
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId, UserDetailsImpl userDetails) {
        Post post = (Post) postDao.findById(postId).orElseThrow(() -> NotFoundPostException.EXCEPTION);

        List<AttachedFile> attachedFiles = postDao.findImagesByPostId(postId);

        PostResponseDto postResponseDto = PostResponseDto.of(post);
        postResponseDto.setAttachedFiles(attachedFiles);

        return postResponseDto;
    }

    @Transactional
    public void toggleWishlist(Long article_id, User user) {
        Long user_id = user.getId();
        if (postDao.isInWishlist(user_id, article_id) > 0) {
            postDao.removeWishlist(user_id, article_id);
        } else {
            postDao.addWishlist(user_id, article_id);
            Long postOwnerId = postDao.getPostOwnerId(article_id);

            NotificationVO notification = new NotificationVO();
            notification.setUserId(postOwnerId);
            notification.setContent(user.getNickName() + "님이 게시글에 찜을 눌렀습니다.");
            notification.setType("WISHLIST");

            defaultNotificationService.createNotification(notification);
        }
    }

    public List<Post> getUserWishlist(User user) {
        return postDao.getUserWishlist(user.getId());
    }

    public boolean isInWishlist(Long userId, Long postId) {
        int count = postDao.isInWishlist(userId, postId);
        return count > 0;
    }

    /**
     * 조회수 증가 기능
     *
     * @param postId
     * @return
     */

    @Transactional
    public int increaseViewCount(Long postId) {
        return postDao.updateCount(postId);
    }



    /**
     * 나의 게시글 기능구현
     *
     * @param postId
     * @param userDetails
     * @return
     */

    @Transactional(readOnly = true)
    public List<PostListResponseDto> getMyPosts(Long postId ,UserDetailsImpl userDetails) {

        List<Post> userPosts = postDao.getPostsByUserId(userDetails.getUser().getId());
        List<PostListResponseDto> dtoList = new ArrayList<>();

        for (Post post : userPosts) {
            dtoList.add(PostListResponseDto.of(post));
        }

        return dtoList;
    }
}
