package ru.itis.services.posts;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.dto.other.LikesPage;
import ru.itis.dto.posts.NewOrUpdateGroupPostDto;
import ru.itis.dto.posts.PostDto;
import ru.itis.dto.posts.PostsPage;
import ru.itis.dto.user.PublicUserDto;
import ru.itis.exceptions.NotFoundException;
import ru.itis.mappers.posts.PostsCollectionMapper;
import ru.itis.mappers.posts.PostsMapper;
import ru.itis.mappers.users.UsersCollectionsMapper;
import ru.itis.models.FileInfo;
import ru.itis.models.Group;
import ru.itis.models.Post;
import ru.itis.models.User;
import ru.itis.repositories.FileInfoRepository;
import ru.itis.repositories.GroupsRepository;
import ru.itis.repositories.PostsRepository;
import ru.itis.services.files.FilesService;
import ru.itis.services.groups.GroupsService;
import ru.itis.services.users.UsersService;
import ru.itis.services.utils.FilesServiceUtils;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostsServiceImpl implements PostsService {
    private final PostsRepository postsRepository;
    private final PostsMapper postsMapper;
    private final GroupsService groupsService;
    private final PostsCollectionMapper postsCollectionMapper;
    private final UsersCollectionsMapper usersCollectionsMapper;
    private final UsersServiceUtils usersServiceUtils;
    private final UsersService usersService;
    private final FilesService filesService;
    private final FilesServiceUtils filesServiceUtils;

    @Value("${default.posts-page-size}")
    private int defaultSize;

    @Override
    public LikesPage getLikes(Long groupId, Long postId) {
        Set<PublicUserDto> users = usersCollectionsMapper
                .toPublicUsersDtoSet(getOrThrow(groupId, postId).getUsersHaveLiked());

        return LikesPage.builder()
                .users(users)
                .totalCount((long) users.size())
                .build();
    }

    @Override
    public Long getLikesCountByPostId(Long postId) {
        getOrThrow(postId);
        return postsRepository.countLikes(postId);
    }

    @Override
    public void putLike(Long groupId, Long postId, String token) {
        Post post = getOrThrow(groupId, postId);
        User user = usersServiceUtils.getUserFromToken(token);

        post.getUsersHaveLiked().add(user);
        user.getLikedPosts().add(post);

        postsRepository.save(post);
    }

    @Override
    public void delete(Long postId, Long groupId) {
        Group group = groupsService.findById(groupId);
//        group.getPosts().remove(getOrThrow(postId));
//        postsRepository.deleteFromPostFile(postId);
        postsRepository.delete(getOrThrow(postId));
    }

    @Override
    public Boolean isUserPutLikeToPost(String username, Long postId, Long groupId) {
        User user = usersService.findByUsername(username);
        getOrThrow(postId);
        groupsService.findById(groupId);
        return postsRepository.isUserPutLikeToPost(user.getId(), postId);
    }

    @Override
    public void removeLike(Long groupId, Long postId, String token) {
        Post post = getOrThrow(groupId, postId);
        User user = usersServiceUtils.getUserFromToken(token);

        post.getUsersHaveLiked().remove(user);
        user.getLikedPosts().remove(post);

        postsRepository.save(post);
    }

    @Override
    public PostsPage getPosts(Long id, int pageNumber) {
        groupsService.findById(id);
        PageRequest pageRequest = PageRequest.of(pageNumber, defaultSize, Sort.by("dateOfPublication").descending());

        Page<Post> posts = postsRepository.findAllByGroupId(pageRequest, id);

        return PostsPage.builder()
                .posts(postsCollectionMapper.toDtoList(posts.getContent()))
                .totalPagesCount(posts.getTotalPages())
                .build();
    }

    @Override
    public PostDto get(Long groupId, Long postId) {
        return postsMapper.toDto(getOrThrow(groupId, postId));
    }

    @Override
    public PostDto add(Long groupId, NewOrUpdateGroupPostDto postDto, String token) {
        Post post = Post.builder()
                .dateOfPublication(new Date())
                .text(postDto.getText())
                .group(groupsService.findById(groupId))
                .author(usersServiceUtils.getUserFromToken(token))
                .files(new HashSet<>())
                .build();

        PostDto newPost = postsMapper.toDto(postsRepository.save(post));

        if (postDto.getFiles() != null) {
            for (MultipartFile file : postDto.getFiles()) {
                String newFileName = filesServiceUtils.generateFileName(file.getOriginalFilename());
                String fileLink = filesService.savePhoto(file, groupId + "/posts/" + newPost.getId() + "/" +
                        newFileName, "groups");

                post.getFiles().add(FileInfo.builder()
                        .fileLink(fileLink)
                        .originalFilename(file.getOriginalFilename())
                        .mimeType(file.getContentType())
                        .build());
            }
        }

        return postsMapper.toDto(postsRepository.save(post));
    }

    @Override
    public PostDto update(Long groupId, Long postId, NewOrUpdateGroupPostDto postDto) {
        Post post = getOrThrow(groupId, postId);

        post.setText(postDto.getText());
        postsRepository.save(post);

        return postsMapper.toDto(post);
    }

    private Post getOrThrow(Long id) {
        return postsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post with id \"" + id + "\" not found"));
    }

    private Post getOrThrow(Long groupId, Long postId) {
        Post post = getOrThrow(postId);
        Group group = groupsService.findById(groupId);

        if (group.getPosts().contains(post)) {
            return post;
        } else {
            throw new NotFoundException("Post with id \"" + postId + "\" not found in group " +
                    "with id \"" + groupId + "\"");
        }
    }

}
