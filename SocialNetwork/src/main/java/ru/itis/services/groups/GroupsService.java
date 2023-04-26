package ru.itis.services.groups;

import ru.itis.dto.group.GroupDto;
import ru.itis.dto.user.UsersPage;
import ru.itis.dto.group.NewOrUpdateGroupDto;
import ru.itis.models.Group;

public interface GroupsService {
    GroupDto findDtoById(Long id);

    Group findById(Long id);

    GroupDto add(NewOrUpdateGroupDto newGroupDto, String rawToken);

    void delete(Long id);

    GroupDto update(Long id, NewOrUpdateGroupDto groupDto);

    UsersPage getUsers(Long id);
}
