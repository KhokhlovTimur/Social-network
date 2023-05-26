package ru.itis.controllers.rest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.posts.PostsPage;

@RequestMapping("/api/posts")
public interface PostsApi {
    @GetMapping("/{username}")
    ResponseEntity<PostsPage> getByUsername(@PathVariable("username") String username,
                                            @RequestParam("page") int pageNumber);
}
