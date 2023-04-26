package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.controllers.rest.api.PostsApi;
import ru.itis.dto.other.LikesPage;
import ru.itis.dto.posts.NewOrUpdateGroupPostDto;
import ru.itis.dto.posts.PostDto;
import ru.itis.dto.posts.PostsPage;
import ru.itis.services.posts.PostsService;

@RestController
@RequiredArgsConstructor
public class PostsController implements PostsApi {
    private final PostsService postsService;

    @Override
    public ResponseEntity<PostDto> addPost(Long id, NewOrUpdateGroupPostDto postDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postsService.add(id, postDto));
    }

    @Override
    public ResponseEntity<PostsPage> getPosts(Long id, int pageNumber) {
        return ResponseEntity.ok(postsService.getPosts(id, pageNumber));
    }

    @Override
    public ResponseEntity<PostDto> updatePost(Long groupId, Long postId, NewOrUpdateGroupPostDto postDto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(postsService.update(groupId, postId, postDto));
    }

    @Override
    public ResponseEntity<PostDto> getPost(Long groupId, Long postId) {
        return ResponseEntity.ok(postsService.get(groupId, postId));
    }

    @Override
    public ResponseEntity<?> removeLike(Long groupId, Long postId) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

    @Override
    public ResponseEntity<LikesPage> getLikes(Long groupId, Long postId) {
        return ResponseEntity.ok(postsService.getEmotions(groupId, postId));
    }

    @Override
    public ResponseEntity<?> putLike(Long groupId, Long postId) {
        postsService.putLike(groupId, postId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
