package ru.itis.services.posts;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.dto.other.LikesPage;
import ru.itis.dto.posts.NewAdminPostDto;
import ru.itis.dto.posts.NewOrUpdateGroupPostDto;
import ru.itis.dto.posts.PostDto;
import ru.itis.dto.posts.PostsPage;
import ru.itis.dto.user.PublicUserDto;
import ru.itis.exceptions.NoAccessException;
import ru.itis.exceptions.NotFoundException;
import ru.itis.mappers.posts.PostsCollectionMapper;
import ru.itis.mappers.posts.PostsMapper;
import ru.itis.mappers.users.UsersCollectionsMapper;
import ru.itis.models.FileInfo;
import ru.itis.models.Group;
import ru.itis.models.Post;
import ru.itis.models.User;
import ru.itis.repositories.PostsRepository;
import ru.itis.services.groups.GroupsService;
import ru.itis.services.users.UsersService;
import ru.itis.services.utils.FilesServiceUtils;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.*;
import java.util.stream.Collectors;

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
    private final FilesServiceUtils filesServiceUtils;

    @Value("${default.posts-page-size}")
    private int defaultSize;

    @Override
    public LikesPage getLikes(Long postId) {
        Set<PublicUserDto> users = usersCollectionsMapper
                .toPublicUsersDtoSet(getOrThrow(postId).getUsersHaveLiked());

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
    public void putLike(Long postId, String token) {
        Post post = getOrThrow(postId);
        User user = usersServiceUtils.getUserFromToken(token);

        post.getUsersHaveLiked().add(user);
        user.getLikedPosts().add(post);

        postsRepository.save(post);
    }

    @Override
    public void delete(Long postId) {
        postsRepository.delete(getOrThrow(postId));
    }

    @Override
    public Boolean isUserPutLikeToPost(String username, Long postId) {
        User user = usersService.findByUsername(username);
        getOrThrow(postId);
        return postsRepository.isUserPutLikeToPost(user.getId(), postId);
    }

    @Override
    public void removeLike(Long postId, String token) {
        Post post = getOrThrow(postId);
        User user = usersServiceUtils.getUserFromToken(token);

        post.getUsersHaveLiked().remove(user);
        user.getLikedPosts().remove(post);

        postsRepository.save(post);
    }

    @Override
    public PostsPage getPostsByToken(String token, int pageNumber) {
        return getPostsByUsername(usersServiceUtils.getUserFromToken(token).getUsername(), pageNumber);
    }

    @Override
    public PostsPage getPostsByUsername(String username, int pageNumber) {
        PageRequest pageable = PageRequest.of(pageNumber, defaultSize, Sort.by("dateOfPublication").descending());

        Page<Post> page = postsRepository.findAllByUsername(pageable, username);
        return PostsPage.builder()
                .posts(addLikesCountAndLikeStatus(page.getContent(), username))
                .totalPagesCount(page.getTotalPages())
                .build();
    }

    @Override
    public PostsPage getPostsByGroupId(Long id, int pageNumber, String token) {
        groupsService.findById(id);
        User user = usersServiceUtils.getUserFromToken(token);

        PageRequest pageRequest = PageRequest.of(pageNumber, defaultSize, Sort.by("dateOfPublication").descending());

        Page<Post> page = postsRepository.findAllByGroupId(pageRequest, id);

        return PostsPage.builder()
                .posts(addLikesCountAndLikeStatus(page.getContent(), user.getUsername()))
                .totalPagesCount(page.getTotalPages())
                .build();
    }

    @Override
    public PostDto get(Long groupId, Long postId) {
        return postsMapper.toDto(getOrThrow(groupId, postId));
    }

    @Override
    public void addAdminPost(NewAdminPostDto postDto, String token) {
        User author = usersServiceUtils.getUserFromToken(token);
        if (author.getRole().equals(User.Role.SUPER_ADMIN) || author.getRole().equals(User.Role.ADMIN)) {
            NewOrUpdateGroupPostDto newOrUpdateGroupPostDto = NewOrUpdateGroupPostDto.builder()
                    .text(postDto.getText())
                    .files(new MultipartFile[]{postDto.getImage()})
                    .build();
            Post post = postsMapper.toPost(add(null, newOrUpdateGroupPostDto, token));
            postsMapper.toDto(postsRepository.save(post));
        } else {
            throw new NoAccessException("Can't save admin post");
        }
    }

    @Override
    public PostDto add(Long groupId, NewOrUpdateGroupPostDto postDto, String token) {
        Post post = Post.builder()
                .dateOfPublication(new Date())
                .text(postDto.getText())
                .author(usersServiceUtils.getUserFromToken(token))
                .files(new HashSet<>())
                .build();

        User creator = usersServiceUtils.getUserFromToken(token);

        if (groupId == null && (creator.getRole().equals(User.Role.ADMIN) || creator.getRole().equals(User.Role.SUPER_ADMIN))) {
            post.setGroup(null);
        } else {
            post.setGroup(groupsService.findById(groupId));
        }
        PostDto newPost = postsMapper.toDto(postsRepository.save(post));

        if (postDto.getFiles() != null) {
            for (MultipartFile file : postDto.getFiles()) {
                String newFileName;
                if (groupId != null) {
                    newFileName = filesServiceUtils.generatePathToFile(file
                    );
                } else {
                    newFileName = filesServiceUtils.generatePathToFile(file
                    );
                }
                post.getFiles().add(FileInfo.builder()
                        .fileLink(newFileName)
                        .originalFilename(file.getOriginalFilename())
                        .mimeType(file.getContentType())
                        .build());
            }
        }

        return postsMapper.toDto(postsRepository.save(post));
    }

    @Override
    public PostDto update(Long postId, NewOrUpdateGroupPostDto postDto) {
        Post post = getOrThrow(postId);

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

    private List<PostDto> addLikesCountAndLikeStatus(List<Post> posts, String username) {
        return postsCollectionMapper.toDtoList(posts)
                .stream()
                .peek(x -> {
                    x.setLikesCount(getLikesCountByPostId(x.getId()));
                    x.setIsLikedByUser(isUserPutLikeToPost(username, x.getId()));
                })
                .collect(Collectors.toList());
    }

}
