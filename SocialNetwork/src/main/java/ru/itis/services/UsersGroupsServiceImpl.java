package ru.itis.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.dto.group.GroupDto;
import ru.itis.mappers.groups.GroupMapper;
import ru.itis.mappers.users.UsersMapper;
import ru.itis.models.Group;
import ru.itis.models.User;
import ru.itis.repositories.GroupsRepository;
import ru.itis.repositories.UsersRepository;
import ru.itis.services.groups.GroupsService;
import ru.itis.services.users.UsersService;

@Service
@RequiredArgsConstructor
public class UsersGroupsServiceImpl implements UsersGroupsService {
    private final UsersService usersService;
    private final GroupsRepository groupsRepository;
    private final GroupsService groupsService;
    private final UsersRepository usersRepository;
    private final GroupMapper groupMapper;

    @Override
    public void deleteUserFromGroup(Long userId, Long groupId) {
        Group group = groupsService.findById(groupId);
        User user = usersService.findById(userId);
        group.getUsers().remove(user);
        user.getGroups().remove(group);

        groupsRepository.save(group);
    }

    @Override
    public GroupDto addGroupToUser(Long userId, Long groupId) {
        User user = usersService.findById(userId);
        Group group = groupsService.findById(groupId);

        user.getGroups().add(group);
        group.getUsers().add(user);

        usersRepository.save(user);

        return groupMapper.toDto(group);
    }
}
