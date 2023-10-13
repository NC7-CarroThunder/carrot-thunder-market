package bitcamp.carrot_thunder.post.service;

import bitcamp.carrot_thunder.NcpObjectStorageService;
import bitcamp.carrot_thunder.config.NcpConfig;
import bitcamp.carrot_thunder.dto.ResponseDto;
import bitcamp.carrot_thunder.post.dto.PostListResponseDto;
import bitcamp.carrot_thunder.post.dto.PostRequestDto;
import bitcamp.carrot_thunder.post.dto.PostResponseDto;
import bitcamp.carrot_thunder.post.dto.PostUpdateRequestDto;
import bitcamp.carrot_thunder.exception.NotHaveAuthorityException;
import bitcamp.carrot_thunder.post.exception.NotFoundPostException;
import bitcamp.carrot_thunder.post.model.vo.DealingType;
import bitcamp.carrot_thunder.post.model.vo.ItemCategory;
import bitcamp.carrot_thunder.post.model.vo.ItemStatus;
import bitcamp.carrot_thunder.secret.UserDetailsImpl;
import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.post.model.dao.PostDao;
import bitcamp.carrot_thunder.post.model.vo.AttachedFile;
import bitcamp.carrot_thunder.post.model.vo.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class DefaultPostService implements PostService {


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



    @Transactional
        public PostResponseDto updatePost(Long postId, PostUpdateRequestDto requestDto, User user, List<MultipartFile> multipartFiles) {
            Post post = (Post) postDao.findById(postId).orElseThrow(() -> NotFoundPostException.EXCEPTION );

            if (!Objects.equals(user.getId(), post.getUser().getId())) {
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


    /**
     * 게시글 삭제
     *
     * @param postId
     * @return
     * @throws Exception ( 난중에 처리 )
     */
    @Transactional
    public String deletePost(Long postId, User user) {
        Post post = (Post) postDao.findById(postId).orElseThrow(() -> NotFoundPostException.EXCEPTION);
        if (user.getId() != post.getUser().getId()) {


        List<AttachedFile> attachedFiles = postDao.findImagesByPostId(post.getId());
        for (AttachedFile attachedFile : attachedFiles) {
            if (!post.getAttachedFiles().isEmpty()) {

                NcpConfig ncpConfig = new NcpConfig();
                NcpObjectStorageService ncpObjectStorageService = new NcpObjectStorageService(ncpConfig);
                ncpObjectStorageService.deleteFile("https://kr.object.ncloudstorage.com", "/carrot-thunder/article" + attachedFile.getFilename());
                // DB에서 이미지 삭제
                postDao.deleteFile(postId);
            }
            }
        }


        postDao.deleteLikes(postId);
        return String.valueOf(postDao.delete(postId));
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
     * @param userDetails
     * @return
     */

    public List<PostListResponseDto> searchPosts(String keyword, UserDetailsImpl userDetails) {
        List<Post> responseDto = postDao.findAll();
        List<PostListResponseDto> searchResults = new ArrayList<>();

        for (Post post : responseDto) {
            if (post.getTitle().contains(keyword)) {
                searchResults.add(PostListResponseDto.of(post));
            }
        }

        return searchResults;
    }

}


