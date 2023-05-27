package ru.itis.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.itis.dto.user.PublicUserDto;
import ru.itis.services.groups.GroupsService;
import ru.itis.services.posts.PostsService;
import ru.itis.services.users.FriendsService;
import ru.itis.services.users.UsersService;

import static ru.itis.security.utils.RequestParsingUtilImpl.AUTHORIZATION_COOKIE;

@Controller
@RequiredArgsConstructor
@RequestMapping("/app")
public class PagesController {
    private final UsersService usersService;
    private final FriendsService friendsService;
    private final GroupsService groupsService;
    private final PostsService postsService;

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
        PublicUserDto user = usersService.getByUsername(username, token);
        model.addAttribute("user", user);
        model.addAttribute("state", friendsService.getStatusNameByTokenAndUsername(token, username));
        model.addAttribute("isMyProfile", usersService.isMyProfile(token, username));
        return "profile";
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
}
