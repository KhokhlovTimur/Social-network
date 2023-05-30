package ru.itis.controllers.rest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.itis.dto.posts.NewAdminPostDto;
import ru.itis.dto.posts.NewOrUpdateGroupPostDto;

@RequestMapping("/api/admin")
public interface AdminsApi {
    @PostMapping("/posts")
    ResponseEntity<?> addAdminPost(@ModelAttribute NewAdminPostDto postDto, @RequestHeader(name = "Authorization") String rawToken);
}
