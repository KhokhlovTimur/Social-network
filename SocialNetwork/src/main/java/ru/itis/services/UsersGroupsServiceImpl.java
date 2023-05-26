package ru.itis.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.itis.dto.group.GroupDto;
import ru.itis.dto.user.UsersPage;
import ru.itis.mappers.groups.GroupMapper;
import ru.itis.mappers.users.UsersCollectionsMapper;
import ru.itis.models.Group;
import ru.itis.models.User;
import ru.itis.repositories.GroupsRepository;
import ru.itis.repositories.UsersRepository;
import ru.itis.services.groups.GroupsService;
import ru.itis.services.users.UsersService;
import ru.itis.services.utils.UsersServiceUtils;

@Service
@RequiredArgsConstructor
public class UsersGroupsServiceImpl implements UsersGroupsService {
    private final GroupsRepository groupsRepository;
    private final GroupsService groupsService;
    private final UsersRepository usersRepository;
    private final GroupMapper groupMapper;
    private final UsersServiceUtils usersServiceUtils;
    private final UsersCollectionsMapper usersCollectionsMapper;

    @Value("${default.page-size}")
    private int pageSize;

    @Override
    public void deleteUserFromGroup(String token, Long groupId) {
        Group group = groupsService.findById(groupId);
        User user = usersServiceUtils.getUserFromToken(token);
        group.getUsers().remove(user);
        user.getGroups().remove(group);

        groupsRepository.save(group);
    }

    @Override
    public UsersPage getUsersFromGroup(Long groupId, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<User> users = usersRepository.findAllByGroupId(groupId, pageRequest);
        return UsersPage.builder()
                .users(usersCollectionsMapper.toPublicUsersDtoSet(users.getContent()))
                .totalCount(users.getTotalElements())
                .pagesCount(users.getTotalPages())
                .build();
    }

    @Override
    public GroupDto addGroupToUser(String token, Long groupId) {
        User user = usersServiceUtils.getUserFromToken(token);
        Group group = groupsService.findById(groupId);

        user.getGroups().add(group);
        group.getUsers().add(user);

        usersRepository.save(user);

        return groupMapper.toDto(group);
    }

}
