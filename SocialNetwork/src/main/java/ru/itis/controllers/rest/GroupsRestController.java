package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.controllers.rest.api.GroupsApi;
import ru.itis.dto.group.GroupDto;
import ru.itis.dto.user.UsersPage;
import ru.itis.dto.group.NewOrUpdateGroupDto;
import ru.itis.services.groups.GroupsService;

@RestController
@RequiredArgsConstructor
public class GroupsRestController implements GroupsApi {
    private final GroupsService groupsService;

    public ResponseEntity<GroupDto> get(Long id) {
        return ResponseEntity.ok()
                .body(groupsService.findDtoById(id));
    }

    public ResponseEntity<GroupDto> add(NewOrUpdateGroupDto newGroupDto, String rawToken) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupsService.add(newGroupDto, rawToken));
    }

    public ResponseEntity<?> delete(Long id) {
        groupsService.delete(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

    public ResponseEntity<GroupDto> update(Long id, NewOrUpdateGroupDto groupDto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(groupsService.update(id, groupDto));
    }

    public ResponseEntity<UsersPage> getUsers(Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(groupsService.getUsers(id));
    }
}
