package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.controllers.rest.api.ChatsApi;
import ru.itis.dto.chats.*;
import ru.itis.dto.user.FriendToChatRequestDto;
import ru.itis.dto.user.UsersPage;
import ru.itis.services.chats.ChatsGlobalIdsService;
import ru.itis.services.chats.ChatsService;
import ru.itis.services.chats.PersonalChatsService;
import ru.itis.services.users.FriendsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatsRestController implements ChatsApi {
    private final ChatsService chatsService;
    private final ChatsGlobalIdsService chatsGlobalIdsService;
    private final PersonalChatsService personalChatsService;
    private final FriendsService friendsService;

    @Override
    public ResponseEntity<List<? extends ChatDtoModel>> getAllByName(String name, String rawToken) {
        return ResponseEntity.ok(chatsGlobalIdsService.getChatsByName(name, rawToken));
    }

    @Override
    public ResponseEntity<ChatDto> addUsersToChat(Long id, FriendToChatRequestDto requestDto) {
        return ResponseEntity.ok(chatsGlobalIdsService.addUsersToChat(id, requestDto));
    }

    @Override
    public ResponseEntity<? extends ChatDtoModel> getByUsernames(String rawToken, String username) {
        return ResponseEntity.ok(personalChatsService.getByTokenAndUsername(rawToken, username));
    }

    @Override
    public ResponseEntity<List<? extends ChatDtoModel>> getAllByToken(String rawToken) {
        return ResponseEntity.ok(chatsGlobalIdsService.getAllChats(rawToken));
    }

    @Override
    public ResponseEntity<? extends ChatDtoModel> getById(Long id, String rawToken) {
        return ResponseEntity.ok(chatsGlobalIdsService.getChatByGlobalId(id, rawToken));
    }

    @Override
    public ResponseEntity<PersonalChatDto> addPersonalChat(String username, String rawToken) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(personalChatsService.add(username, rawToken));
    }

    @Override
    public ResponseEntity<ChatDto> addChat(NewOrUpdateChatDto chatDto, String rawToken) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatsService.add(chatDto, rawToken));
    }

    @Override
    public ResponseEntity<UsersPage> getAllFriends(String username, Long id) {
        return ResponseEntity.ok(friendsService.getAllFriendsInChat(username, id));
    }
}
