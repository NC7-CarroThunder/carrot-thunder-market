package bitcamp.carrot_thunder.post.service;

import bitcamp.carrot_thunder.NcpObjectStorageService;
import bitcamp.carrot_thunder.chatting.model.dao.ChattingDAO;
import bitcamp.carrot_thunder.chatting.model.vo.ChatRoomVO;
import bitcamp.carrot_thunder.exception.NotHaveAuthorityException;
import bitcamp.carrot_thunder.post.dto.PostListResponseDto;
import bitcamp.carrot_thunder.post.dto.PostRequestDto;
import bitcamp.carrot_thunder.post.dto.PostResponseDto;
import bitcamp.carrot_thunder.post.dto.PostUpdateRequestDto;
import bitcamp.carrot_thunder.post.exception.NotFoundPostException;
import bitcamp.carrot_thunder.post.model.vo.*;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.post.model.dao.PostDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
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


    @Override
    public PostResponseDto createPost(PostRequestDto postRequestDto, MultipartFile[] files, UserDetailsImpl userDetails) throws Exception {
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

    @Transactional
    public PostResponseDto updatePost(Long postId, PostUpdateRequestDto requestDto, User user) {

        Post post =  postDao.findById(postId).orElseThrow(() -> NotFoundPostException.EXCEPTION);


        if (!Objects.equals(user.getNickName(), post.getUser().getNickName())) {
            throw NotHaveAuthorityException.EXCEPTION;
        }



//        List<String> uploadedImageUrls = new ArrayList<>();

//        for (MultipartFile file : files) {
//            String imageUrl = ncpObjectStorageService.uploadFile("carrot-thunder", "article/", file);
//            uploadedImageUrls.add(imageUrl);
//        }



//        if (!post.getAttachedFiles().isEmpty()) {
//            postDao.insertFiles(post);
//        } else {
//            throw No
//        }


        post.update(requestDto);
        postDao.update(post);
//        List<String> remainingImages = getRemainingImages(requestDto);
//
//        handleImageUpdates(post, remainingImages);
        return PostResponseDto.of(post);
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
    public List<PostListResponseDto> getPostlist(User user, UserDetailsImpl userDetails) {

        List<Post> posts = postDao.findAll();
        List<PostListResponseDto> dtoList = new ArrayList<>();

        for (Post post : posts) {
            PostListResponseDto responseDto = PostListResponseDto.of(post);

            if (userDetails != null) {
                boolean isLiked = postDao.isLiked(post.getId(), user.getId());
                responseDto.setIsLiked(isLiked);
            }
            dtoList.add(responseDto);
        }

        return dtoList;
    }

    public AttachedFile getAttachedFile(Long fileId) throws Exception {
        return postDao.findFileByfileId(fileId);
    }



    public int increaseViewCount(Long postId)  {
        return postDao.updateCount(postId);
    }


    //TODO : 로그인 유저 , 게시글 유저 비교 필요
    /**
     * 게시글 삭제
     *
     * @param postId
     * @return
     * @throws Exception ( 난중에 처리 )
     */
    @Transactional
    public int deletePost(Long postId, User user) {

        Post post = postDao.findById(postId).orElseThrow(() -> NotFoundPostException.EXCEPTION);
        String roomId = chattingDao.getRoomIdByPostId(postId);

        if (!Objects.equals(user.getNickName(), post.getUser().getNickName())) {
            throw NotHaveAuthorityException.EXCEPTION;
        }


        List<AttachedFile> attachedFiles = postDao.findImagesByPostId(post.getId());
        for (AttachedFile attachedFile : attachedFiles) {
            if (!post.getAttachedFiles().isEmpty()) {
                // S3에서 이미지 삭제
                ncpObjectStorageService.deleteFile("carrot-thunder", "article/" + attachedFile.getFilePath());
                // DB에서 이미지 삭제
                postDao.deleteFile(attachedFile.getId());
            }
//            postDao.deleteLikes()
        }

            chattingDao.deleteChatMsgByRoomId(roomId);

            chattingDao.deleteChatRoomByPostId(postId);
        return postDao.delete(postId);

    }



    /**
     * 게시글 상세 정보
     *
     * @param postId
     * @return
     */
    @Override
    public PostResponseDto getPost(Long postId, UserDetailsImpl userDetails) {
        Post post = postDao.findById(postId).orElseThrow(() -> NotFoundPostException.EXCEPTION);

        List<AttachedFile> attachedFiles = postDao.findImagesByPostId(postId);

        PostResponseDto postResponseDto = PostResponseDto.of(post);
        postResponseDto.setAttachedFiles(attachedFiles);

        return postResponseDto;
    }


}