package ru.itis.services.posts;

import ru.itis.dto.other.LikesPage;
import ru.itis.dto.posts.NewAdminPostDto;
import ru.itis.dto.posts.NewOrUpdateGroupPostDto;
import ru.itis.dto.posts.PostDto;
import ru.itis.dto.posts.PostsPage;

public interface PostsService {
    PostDto add(Long groupId, NewOrUpdateGroupPostDto postDto, String token);

    void addAdminPost(NewAdminPostDto postDto, String token);

    PostsPage getPostsByGroupId(Long id, int pageNumber, String token);

    PostDto get(Long groupId, Long postId);

    PostDto update(Long postId, NewOrUpdateGroupPostDto postDto);

    LikesPage getLikes(Long postId);

    void putLike(Long postId, String token);

    Long getLikesCountByPostId(Long postId);

    void removeLike(Long postId, String token);

    Boolean isUserPutLikeToPost(String username, Long postId);

    PostsPage getPostsByUsername(String username, int pageNumber);

    PostsPage getPostsByToken(String token, int pageNumber);

    void delete(Long postId);
}
