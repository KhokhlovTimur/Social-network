package ru.itis.services;

import ru.itis.dto.group.GroupDto;
import ru.itis.dto.user.UsersPage;

public interface UsersGroupsService {
    void deleteUserFromGroup(String token, Long groupId);

    GroupDto addGroupToUser(String token, Long groupId);

    UsersPage getUsersFromGroup(Long groupId, int pageNumber);
}
