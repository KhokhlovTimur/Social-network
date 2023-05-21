package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.controllers.rest.api.UsersGroupsApi;
import ru.itis.dto.group.GroupDto;
import ru.itis.dto.group.GroupsPage;
import ru.itis.dto.user.UsersPage;
import ru.itis.services.UsersGroupsService;
import ru.itis.services.groups.GroupsService;

@RestController
@RequiredArgsConstructor
public class UsersGroupRestController implements UsersGroupsApi {
    private final UsersGroupsService usersGroupsService;
    private final GroupsService groupsService;

    public ResponseEntity<?> deleteUserFromGroup(Long groupId, String token) {
        usersGroupsService.deleteUserFromGroup(token, groupId);
        return ResponseEntity.accepted()
                .build();
    }

    @Override
    public ResponseEntity<GroupsPage> getGroups(int pageNumber, String username) {
        return ResponseEntity.ok(groupsService.getGroupsByUsername(username, pageNumber));
    }

    @Override
    public ResponseEntity<GroupsPage> getGroups(String name, int pageNumber) {
        return ResponseEntity.ok(groupsService.getGroupsByUsernameAndNameLike(name, pageNumber));
    }

    @Override
    public ResponseEntity<UsersPage> getUsers(Long groupId, int pageNumber) {
        return ResponseEntity.ok(usersGroupsService.getUsersFromGroup(groupId, pageNumber));
    }

    @Override
    public ResponseEntity<GroupDto> addGroupToUser(Long groupId, String rawToken) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usersGroupsService.addGroupToUser(rawToken, groupId));
    }
}
