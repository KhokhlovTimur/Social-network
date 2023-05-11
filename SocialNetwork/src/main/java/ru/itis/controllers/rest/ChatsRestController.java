package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.controllers.rest.api.ChatsApi;
import ru.itis.dto.chats.ChatDto;
import ru.itis.dto.chats.ChatDtoModel;
import ru.itis.dto.chats.NewOrUpdateChatDto;
import ru.itis.services.chats.ChatsGlobalIdsService;
import ru.itis.services.chats.ChatsService;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class ChatsRestController implements ChatsApi {
    private final ChatsService chatsService;
    private final ChatsGlobalIdsService chatsGlobalIdsService;

    @Override
    public ResponseEntity<Set<? extends ChatDtoModel>> getByName(String name, String rawToken) {
        return ResponseEntity.ok(chatsGlobalIdsService.getChatsByName(name, rawToken));
    }

    @Override
    public ResponseEntity<Set<? extends ChatDtoModel>> getAll(String rawToken) {
        return ResponseEntity.ok(chatsGlobalIdsService.getAllChats(rawToken));
    }

    @Override
    public ResponseEntity<? extends ChatDtoModel> get(Long id, String rawToken) {
        return ResponseEntity.ok(chatsGlobalIdsService.getChatByGlobalId(id, rawToken));
    }

    @Override
    public ResponseEntity<ChatDto> add(NewOrUpdateChatDto chatDto, String rawToken) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatsService.add(chatDto, rawToken));
    }
}
