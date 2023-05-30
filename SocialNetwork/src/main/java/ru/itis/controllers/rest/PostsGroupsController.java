package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.controllers.rest.api.PostsGroupsApi;
import ru.itis.dto.other.LikesPage;
import ru.itis.dto.posts.NewOrUpdateGroupPostDto;
import ru.itis.dto.posts.PostDto;
import ru.itis.dto.posts.PostsPage;
import ru.itis.services.posts.PostsService;

@RestController
@RequiredArgsConstructor
public class PostsGroupsController implements PostsGroupsApi {
    private final PostsService postsService;

    @Override
    public ResponseEntity<PostDto> addPost(Long id, MultipartFile[] files, String text, String rawToken) {
        NewOrUpdateGroupPostDto postDto = NewOrUpdateGroupPostDto.builder()
                .files(files)
                .text(text)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postsService.add(id, postDto, rawToken));
    }

    @Override
    public ResponseEntity<PostsPage> getPosts(Long id, int pageNumber, String rawToken) {
        return ResponseEntity.ok(postsService.getPostsByGroupId(id, pageNumber, rawToken));
    }

    @Override
    public ResponseEntity<PostDto> updatePost(Long groupId, Long postId, NewOrUpdateGroupPostDto postDto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(postsService.update(postId, postDto));
    }

    @Override
    public ResponseEntity<PostDto> getPost(Long groupId, Long postId) {
        return ResponseEntity.ok(postsService.get(groupId, postId));
    }

    @Override
    public ResponseEntity<?> delete(Long groupId, Long postId) {
        postsService.delete(postId);
        return ResponseEntity.accepted()
                .build();
    }

    @Override
    public ResponseEntity<Boolean> isUserPutLikeToPost(Long groupId, Long postId, String username) {
        return ResponseEntity.ok(postsService.isUserPutLikeToPost(username, postId));
    }

    @Override
    public ResponseEntity<Long> getLikesCount(Long groupId, Long postId) {
        return ResponseEntity.ok(postsService.getLikesCountByPostId(postId));
    }

    @Override
    public ResponseEntity<?> removeLike(Long groupId, Long postId, String rawToken) {
        postsService.removeLike(postId, rawToken);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

    @Override
    public ResponseEntity<LikesPage> getLikes(Long groupId, Long postId) {
        return ResponseEntity.ok(postsService.getLikes(postId));
    }

    @Override
    public ResponseEntity<?> putLike(Long groupId, Long postId, String rawToken) {
        postsService.putLike(postId, rawToken);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
