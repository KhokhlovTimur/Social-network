package ru.itis.services.users;

import ru.itis.dto.group.GroupDto;
import ru.itis.dto.user.*;
import ru.itis.models.User;

public interface UsersService {
    User findById(Long id);

    PrivateUserDto signUp(UserSignUpDto form);

    void banUser(Long id);

    PrivateUserDto update(Long id, UserUpdateDto userDto);

    GroupsPage getGroups(Long userId);

    <T extends PublicUserDto> T getById(Long id, String token);

}
