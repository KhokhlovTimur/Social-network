package ru.itis.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.posts.NewAdminPostDto;
import ru.itis.dto.posts.NewOrUpdateGroupPostDto;
import ru.itis.dto.user.PublicUserDto;
import ru.itis.services.groups.GroupsService;
import ru.itis.services.posts.PostsService;
import ru.itis.services.users.FriendsService;
import ru.itis.services.users.UsersService;
import ru.itis.services.utils.PagesModelsUtils;
import ru.itis.services.utils.UsersServiceUtils;

import static ru.itis.security.utils.RequestParsingUtilImpl.AUTHORIZATION_COOKIE;

@Controller
@RequiredArgsConstructor
@RequestMapping("/app")
public class PagesController {
    private final FriendsService friendsService;
    private final GroupsService groupsService;
    private final PostsService postsService;
    private final PagesModelsUtils pagesModelsUtils;

    @GetMapping("/login")
    public String getAuthPage() {
        return "authentication";
    }

    @GetMapping("/feeds")
    public String getFeedsPage(Model model, @CookieValue(AUTHORIZATION_COOKIE) String token) {
        model.addAttribute("posts", postsService.getPostsByToken(token, 0).getPosts());
        return "feeds";
    }

    @GetMapping("/profile/{username}")
    public String getProfilePage(Model model, @CookieValue(AUTHORIZATION_COOKIE) String token, @PathVariable("username") String username) {
        return pagesModelsUtils.getViewNameByUsername(username, model, token);
    }

    @GetMapping("/groups")
    public String getGroupsPage(Model model, @CookieValue(AUTHORIZATION_COOKIE) String token) {
        model.addAttribute("groups", groupsService.getGroupsByToken(token, 0).getGroups());
        return "groups";
    }

    @GetMapping("/friends")
    public String getFriendsPage(Model model, @CookieValue(AUTHORIZATION_COOKIE) String token) {
        model.addAttribute("friends", friendsService.getFriendsByToken(token, "friends", "", 0).getUsers());
        return "friends";
    }

    @GetMapping("/admin/posts")
    public String getAdminPostsPage(@CookieValue(AUTHORIZATION_COOKIE) String token) {
        return "admin-posts";
    }

    @PostMapping("/admin/posts")
    public String addAdminPost(@ModelAttribute NewAdminPostDto postDto, @CookieValue(AUTHORIZATION_COOKIE) String token) {
        postsService.addAdminPost(postDto, token);
        return "redirect:/app/admin/posts";
    }
}
