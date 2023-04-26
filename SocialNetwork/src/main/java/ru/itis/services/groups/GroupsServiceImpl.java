package ru.itis.services.groups;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.dto.group.GroupDto;
import ru.itis.dto.group.NewOrUpdateGroupDto;
import ru.itis.dto.user.UsersPage;
import ru.itis.dto.user.PublicUserDto;
import ru.itis.exceptions.NotFoundException;
import ru.itis.mappers.groups.GroupMapper;
import ru.itis.mappers.users.UsersCollectionsMapping;
import ru.itis.models.Group;
import ru.itis.repositories.GroupsRepository;
import ru.itis.repositories.UsersRepository;
import ru.itis.security.utils.AuthorizationsHeaderUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupsServiceImpl implements GroupsService {
    private final GroupsRepository groupsRepository;
    private final GroupMapper groupMapper;
    private final UsersCollectionsMapping usersCollectionsMapping;
    private final AuthorizationsHeaderUtil authorizationsHeaderUtil;
    private final UsersRepository usersRepository;

    @Override
    public GroupDto findDtoById(Long id) {
        return groupMapper.toDto(getOrThrow(id));
    }

    @Override
    public Group findById(Long id) {
        return getOrThrow(id);
    }

    @Override
    public GroupDto add(NewOrUpdateGroupDto newGroupDto, String rawToken) {
        Group group = Group.builder()
                .name(newGroupDto.getName())
                .description(newGroupDto.getDescription())
                .dateOfCreation(new Date())
                .status(Group.Status.ACTIVE)
                .creator(usersRepository.findByUsername(authorizationsHeaderUtil
                        .getDataFromToken(rawToken).get("username")).orElseThrow())
                .users(new HashSet<>())
                .build();

        groupsRepository.save(group);
        return groupMapper.toDto(group);
    }

    @Override
    public void delete(Long id) {
        Group group = getOrThrow(id);
        group.setStatus(Group.Status.DELETED);

        groupsRepository.save(group);
    }

    @Override
    public GroupDto update(Long id, NewOrUpdateGroupDto groupDto) {
        Group group = getOrThrow(id);

        group.setDescription(groupDto.getDescription());
        group.setName(groupDto.getName());

        groupsRepository.save(group);
        return groupMapper.toDto(group);
    }

    @Override
    public UsersPage getUsers(Long id) {
        Group group = getOrThrow(id);
        Set<PublicUserDto> users = usersCollectionsMapping.toGroupDtoSet(group.getUsers()
                .stream()
                .filter(x -> !x.isBanned())
                .collect(Collectors.toSet()));

        return UsersPage.builder()
                .users(users)
                .totalCount(users.size())
                .build();
    }

    private Group getOrThrow(Long id) {
        Group group = groupsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Group with id <" + id + "> not found"));

        if (!group.isActive()) {
            throw new NotFoundException("Group with id <" + id + "> is not active");
        }

        return group;
    }

}
