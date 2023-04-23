package ru.itis.services.posts;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.itis.dto.other.LikesPage;
import ru.itis.dto.posts.NewOrUpdateGroupPostDto;
import ru.itis.dto.posts.PostDto;
import ru.itis.dto.posts.PostsPage;
import ru.itis.dto.user.PublicUserDto;
import ru.itis.exceptions.NotFoundException;
import ru.itis.mappers.posts.PostsCollectionMapper;
import ru.itis.mappers.posts.PostsMapper;
import ru.itis.mappers.users.UsersCollectionsMapping;
import ru.itis.models.Group;
import ru.itis.models.Post;
import ru.itis.models.User;
import ru.itis.repositories.PostsRepository;
import ru.itis.services.groups.GroupsService;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostsServiceImpl implements PostsService {
    private final PostsRepository postsRepository;
    private final PostsMapper postsMapper;
    private final GroupsService groupsService;
    private final PostsCollectionMapper postsCollectionMapper;
    private final UsersCollectionsMapping usersCollectionsMapping;
    private final UsersServiceUtils usersServiceUtils;

    @Value("${default.page-size}")
    private int defaultSize;

    @Override
    public LikesPage getEmotions(Long groupId, Long postId) {
        Set<PublicUserDto> users = usersCollectionsMapping
                .toGroupDtoSet(getOrThrow(groupId, postId).getUsersHaveLiked());

        return LikesPage.builder()
                .users(users)
                .totalCount((long) users.size())
                .build();
    }

    @Override
    public void putLike(Long groupId, Long postId) {
        Post post = getOrThrow(groupId, postId);
        User user = usersServiceUtils.getUserFromContext();

        post.getUsersHaveLiked().add(user);
        user.getLikedPosts().add(post);

        postsRepository.save(post);
    }

    @Override
    public void removeLike(Long groupId, Long postId) {
        Post post = getOrThrow(groupId, postId);
        User user = usersServiceUtils.getUserFromContext();

        post.getUsersHaveLiked().remove(user);
        user.getLikedPosts().remove(post);

        postsRepository.save(post);
    }

    @Override
    public PostsPage getPosts(Long id, int pageNumber) {
        groupsService.findById(id);
        PageRequest pageRequest = PageRequest.of(pageNumber, defaultSize);

        Page<Post> posts = postsRepository.findAllByGroupIdOrderById(pageRequest, id);

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
    public PostDto add(Long groupId, NewOrUpdateGroupPostDto postDto) {
        Post post = Post.builder()
                .dateOfPublication(new Date())
                .text(postDto.getText())
                .group(groupsService.findById(groupId))
                .user(usersServiceUtils.getUserFromContext())
                .build();

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
                .orElseThrow(() -> new NotFoundException("Post with id <" + id + "> not found"));
    }

    private Post getOrThrow(Long groupId, Long postId) {
        Post post = getOrThrow(postId);
        Group group = groupsService.findById(groupId);

        if (group.getPosts().contains(post)) {
            return post;
        } else {
            throw new NotFoundException("Post with id <" + postId + "> not found in group " +
                    "with id <" + groupId + ">");
        }
    }
}
