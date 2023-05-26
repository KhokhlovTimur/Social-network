package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.controllers.rest.api.UsersApi;
import ru.itis.dto.user.*;
import ru.itis.services.users.FriendsService;
import ru.itis.services.users.UsersService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class UsersRestController implements UsersApi {
    private final UsersService usersService;
    private final FriendsService friendsService;

    @Override
    public ResponseEntity<PrivateUserDto> signUp(UserSignUpDto userSignUpDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usersService.signUp(userSignUpDto));
    }

    @Override
    public ResponseEntity<? extends PublicUserDto> get(String username, String token) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(usersService.getByUsername(username, token));
    }

    @Override
//    @TokenValid
    public ResponseEntity<?> delete(Long id, String rawToken) {
        usersService.banUser(id);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

    @Override
//    @TokenValid
    public ResponseEntity<UserUpdateResponseDto> update(String username, UserUpdateDto updateUserDto, String rawToken, HttpServletResponse response) {
        return ResponseEntity.accepted()
                .body(usersService.update(username, updateUserDto, response));
    }

//    @Override
//    public ResponseEntity<List<PublicUserDto>> getFriends(Long id) {
//        return ResponseEntity.ok(friendsService.getFriends(id));
//    }

}
