package ru.itis.services.users;

import ru.itis.dto.group.GroupsPage;
import ru.itis.dto.posts.PostDto;
import ru.itis.dto.user.*;
import ru.itis.models.User;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public interface UsersService {
    User findById(Long id);

    PrivateUserDto signUp(UserSignUpDto form);

    void banUser(Long id);

    UserUpdateResponseDto update(String username, UserUpdateDto userDto, HttpServletResponse response);

    GroupsPage getGroups(Long userId);

    <T extends PublicUserDto> T getByIdAndToken(Long id, String token);

    Set<PostDto> getPostsFromGroups(String token);

    User findByUsername(String username);

    <T extends PublicUserDto> T getByUsername(String username, String token);

    boolean isMyProfile(String token, String username);

    boolean isUsernameExists(String username);
}
