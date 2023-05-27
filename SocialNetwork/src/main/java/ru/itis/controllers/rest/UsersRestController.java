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
    public ResponseEntity<?> delete(Long id, String rawToken) {
        usersService.banUser(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

    @Override
    public ResponseEntity<UserUpdateResponseDto> update(String username, UserUpdateDto updateUserDto, String rawToken, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(usersService.update(username, updateUserDto, response));
    }

    @Override
    public ResponseEntity<?> deleteFriend(String username, String friendUsername) {
        friendsService.deleteFriendOrRevokeRequest(username, friendUsername);
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<UsersPage> getFriends(String username, String type, int pageNumber, String query) {
        return ResponseEntity.ok(friendsService.getRequestsOrFriends(username, type, query, pageNumber));
    }

    @Override
    public ResponseEntity<FriendResponseDto> addFriend(String username, String friendUsername) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(friendsService.sendFriendRequest(username, friendUsername));
    }

}
