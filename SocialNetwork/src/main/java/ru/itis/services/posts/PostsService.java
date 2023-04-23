package ru.itis.services.posts;

import ru.itis.dto.other.LikesPage;
import ru.itis.dto.posts.NewOrUpdateGroupPostDto;
import ru.itis.dto.posts.PostDto;
import ru.itis.dto.posts.PostsPage;
import ru.itis.models.Post;

import java.util.List;

public interface PostsService {
    PostDto add(Long groupId, NewOrUpdateGroupPostDto postDto);

    PostsPage getPosts(Long id, int pageNumber);

    PostDto get(Long groupId, Long postId);

    PostDto update(Long groupId, Long postId, NewOrUpdateGroupPostDto postDto);

    LikesPage getEmotions(Long groupId, Long postId);

    void putLike(Long groupId, Long postId);

    void removeLike(Long groupId, Long postId);
}
