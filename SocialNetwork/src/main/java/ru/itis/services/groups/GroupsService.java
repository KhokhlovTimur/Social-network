package ru.itis.services.groups;

import ru.itis.dto.group.GroupDto;
import ru.itis.dto.group.GroupsPage;
import ru.itis.dto.user.UsersPage;
import ru.itis.dto.group.NewOrUpdateGroupDto;
import ru.itis.models.Group;

public interface GroupsService {
    GroupDto findDtoById(Long id);

    GroupsPage getGroupsByToken(String token, int pageNumber);

    GroupsPage getGroupsByUsername(String username, int pageNumber);

    GroupsPage getGroupsByUsernameAndNameLike(String name, int pageNumber);

    boolean isUserExistsInGroup(String username, Long id);

    Group findById(Long id);

    GroupDto add(NewOrUpdateGroupDto newGroupDto, String rawToken);

    void delete(Long id);

    GroupDto update(Long id, NewOrUpdateGroupDto groupDto);

    UsersPage getUsers(Long id);

    boolean isNameOccupied(String name);
}
