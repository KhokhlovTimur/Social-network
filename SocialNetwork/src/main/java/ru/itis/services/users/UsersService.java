package ru.itis.services.users;

import ru.itis.dto.chats.PersonalChatDto;
import ru.itis.dto.group.GroupDto;
import ru.itis.dto.group.GroupsPage;
import ru.itis.dto.posts.PostDto;
import ru.itis.dto.user.*;
import ru.itis.models.Group;
import ru.itis.models.User;

import java.util.List;
import java.util.Set;

public interface UsersService {
    User findById(Long id);

    PrivateUserDto signUp(UserSignUpDto form);

    void banUser(Long id);

    PrivateUserDto update(Long id, UserUpdateDto userDto);

    GroupsPage getGroups(Long userId);

    <T extends PublicUserDto> T getByIdAndToken(Long id, String token);

    Set<PostDto> getPostsFromGroups(String token);

    User findByUsername(String username);

    boolean isMyProfile(String token, Long id);
}
