package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.controllers.rest.api.GroupsApi;
import ru.itis.dto.group.GroupDto;
import ru.itis.dto.group.NewOrUpdateGroupDto;
import ru.itis.services.groups.GroupsService;

@RestController
@RequiredArgsConstructor
public class GroupsRestController implements GroupsApi {
    private final GroupsService groupsService;

    @Override
    public ResponseEntity<GroupDto> get(Long id) {
        return ResponseEntity.ok()
                .body(groupsService.findDtoById(id));
    }

    @Override
    public ResponseEntity<GroupDto> add(String description, String name, MultipartFile image,
                                        String rawToken) {
        NewOrUpdateGroupDto groupDto = NewOrUpdateGroupDto.builder()
                .description(description)
                .name(name)
                .image(image)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupsService.add(groupDto, rawToken));
    }

    @Override
    public ResponseEntity<?> delete(Long id) {
        groupsService.delete(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

    @Override
    public ResponseEntity<Boolean> isUserExists(Long id, String username) {
        return ResponseEntity.ok(groupsService.isUserExistsInGroup(username, id));
    }

    @Override
    public ResponseEntity<GroupDto> update(Long id, String description, String name, MultipartFile image, String rawToken) {
        NewOrUpdateGroupDto groupDto = NewOrUpdateGroupDto.builder()
                .description(description)
                .name(name)
                .image(image)
                .build();
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(groupsService.update(id, groupDto));
    }

}
