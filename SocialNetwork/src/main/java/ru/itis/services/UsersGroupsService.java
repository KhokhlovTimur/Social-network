package ru.itis.services;

import ru.itis.dto.group.GroupDto;

import java.util.List;

public interface UsersGroupsService {
    void deleteUserFromGroup(Long userId, Long groupId);

    GroupDto addGroupToUser(Long userId, Long groupId);
}
