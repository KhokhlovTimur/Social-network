package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.controllers.rest.api.PostsApi;
import ru.itis.dto.posts.PostsPage;
import ru.itis.services.posts.PostsService;

@RequiredArgsConstructor
@RestController
public class PostsRestController implements PostsApi {
    private final PostsService postsService;

    @Override
    public ResponseEntity<PostsPage> getByUsername(String username, int pageNumber) {
        return ResponseEntity.ok(postsService.getPostsByUsername(username, pageNumber));
    }
}
