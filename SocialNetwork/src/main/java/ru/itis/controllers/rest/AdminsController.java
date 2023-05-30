package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.controllers.rest.api.AdminsApi;
import ru.itis.dto.posts.NewAdminPostDto;
import ru.itis.services.posts.PostsService;

@RestController
@RequiredArgsConstructor
public class AdminsController implements AdminsApi {
    private final PostsService postsService;

    @Override
    public ResponseEntity<?> addAdminPost(NewAdminPostDto postDto, String rawToken) {
        postsService.addAdminPost(postDto, rawToken);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
