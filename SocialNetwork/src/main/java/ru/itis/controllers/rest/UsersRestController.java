package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.annotations.TokenValid;
import ru.itis.controllers.rest.api.UsersApi;
import ru.itis.dto.group.GroupsPage;
import ru.itis.dto.user.*;
import ru.itis.services.users.UsersService;

@RestController
@RequiredArgsConstructor
public class UsersRestController implements UsersApi {
    private final UsersService usersService;

    @Override
    public ResponseEntity<PrivateUserDto> signUp(UserSignUpDto userSignUpDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usersService.signUp(userSignUpDto));
    }

    @Override
    public ResponseEntity<? extends PublicUserDto> get(Long id, String rawToken) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(usersService.getById(id, rawToken));
    }

    @Override
    @TokenValid
    public ResponseEntity<?> delete(Long id, String rawToken) {
        usersService.banUser(id);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

    @Override
    @TokenValid
    public ResponseEntity<PrivateUserDto> update(Long id, UserUpdateDto updateUserDto, String rawToken) {
        return ResponseEntity.accepted()
                .body(usersService.update(id, updateUserDto));
    }

    @Override
    public ResponseEntity<GroupsPage> getGroups(Long id) {
        return ResponseEntity.ok(usersService.getGroups(id));
    }
}
