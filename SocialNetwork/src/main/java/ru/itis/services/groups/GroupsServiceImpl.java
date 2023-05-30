package ru.itis.services.groups;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.itis.dto.group.GroupDto;
import ru.itis.dto.group.GroupsPage;
import ru.itis.dto.group.NewOrUpdateGroupDto;
import ru.itis.dto.user.UsersPage;
import ru.itis.dto.user.PublicUserDto;
import ru.itis.exceptions.AlreadyExistsException;
import ru.itis.exceptions.NotFoundException;
import ru.itis.mappers.groups.GroupCollectionsMapper;
import ru.itis.mappers.groups.GroupMapper;
import ru.itis.mappers.users.UsersCollectionsMapper;
import ru.itis.models.Group;
import ru.itis.models.User;
import ru.itis.repositories.GroupsRepository;
import ru.itis.services.users.UsersService;
import ru.itis.services.utils.FilesServiceUtils;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupsServiceImpl implements GroupsService {
    private final GroupsRepository groupsRepository;
    private final GroupMapper groupMapper;
    private final UsersCollectionsMapper usersCollectionsMapper;
    private final UsersServiceUtils usersServiceUtils;
    private final GroupCollectionsMapper groupCollectionsMapper;
    private final UsersService usersService;
    private final FilesServiceUtils filesServiceUtils;

    @Value("${default.page-size}")
    private int pageSize;

    @Override
    public GroupsPage getGroupsByUsernameAndNameLike(String name, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Group> page = groupsRepository.findByNameLike(name, pageRequest);
        return GroupsPage.builder()
                .groups(groupCollectionsMapper.toGroupDtoSet(page.getContent()))
                .totalCount(page.getTotalElements())
                .pagesCount(page.getTotalPages())
                .build();
    }

    @Override
    public GroupsPage getGroupsByUsername(String username, int pageNumber) {
        User user = usersService.findByUsername(username);
        return getByUser(user, pageNumber);
    }

    @Override
    public GroupsPage getGroupsByToken(String token, int pageNumber) {
        User user = usersServiceUtils.getUserFromToken(token);
        return getByUser(user, pageNumber);
    }

    private GroupsPage getByUser(User user, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Group> groups = groupsRepository.findAllByUserId(user.getId(), pageRequest);

        return GroupsPage.builder()
                .groups(groupCollectionsMapper.toGroupDtoSet(groups.getContent()))
                .pagesCount(groups.getTotalPages())
                .build();
    }

    @Override
    public GroupDto findDtoById(Long id) {
        return groupMapper.toDto(getOrThrow(id));
    }

    @Override
    public boolean isUserExistsInGroup(String username, Long id) {
        return groupsRepository.isUserExistsInGroup(username, id);
    }

    @Override
    public Group findById(Long id) {
        return getOrThrow(id);
    }

    @Override
    public GroupDto add(NewOrUpdateGroupDto newGroupDto, String rawToken) {
        checkGroupName(newGroupDto.getName());
        User creator = (usersServiceUtils.getUserFromToken(rawToken));

        Group group = Group.builder()
                .name(newGroupDto.getName())
                .description(newGroupDto.getDescription())
                .dateOfCreation(new Date())
                .status(Group.Status.ACTIVE)
                .creator(creator)
                .users(new HashSet<>())
                .build();

        groupsRepository.save(group);

        creator.getGroups().add(group);
        group.getUsers().add(creator);

        group.setImageLink(filesServiceUtils.generatePathToFile(newGroupDto.getImage()
        ));
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
        System.out.println(groupDto);

        if (groupDto.getName() != null && !groupDto.getName().equals(group.getName())) {
            checkGroupName(groupDto.getName());
            group.setName(groupDto.getName());
        }

        if (groupDto.getDescription() != null) {
            group.setDescription(groupDto.getDescription());
        }
        if (groupDto.getImage() != null) {
            group.setImageLink(filesServiceUtils.generatePathToFile(groupDto.getImage()
            ));
        }

        groupsRepository.save(group);
        return groupMapper.toDto(group);
    }

    @Override
    public boolean isNameOccupied(String name) {
        return groupsRepository.existsByName(name);
    }

    @Override
    public UsersPage getUsers(Long id) {
        Group group = getOrThrow(id);
        Set<PublicUserDto> users = usersCollectionsMapper.toPublicUsersDtoSet(group.getUsers()
                .stream()
                .filter(x -> !x.isBanned())
                .collect(Collectors.toSet()));

        return UsersPage.builder()
                .users(users)
                .totalCount(users.size())
                .build();
    }

    private Group getOrThrow(Long id) {
        if (id == null) {
            throw new NotFoundException("Group not found");
        }
        Group group = groupsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Group with id \"" + id + "\" not found"));

        if (!group.isActive()) {
            throw new NotFoundException("Group with id \"" + id + "\" is not active");
        }

        return group;
    }

    private void checkGroupName(String name) {
        if (groupsRepository.existsByName(name)) {
            throw new AlreadyExistsException("Group with name \"" + name + "\" already exists");
        }
    }

}
