package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.controllers.rest.api.ChatsApi;
import ru.itis.dto.chats.ChatDto;
import ru.itis.dto.chats.NewOrUpdateChatDto;
import ru.itis.services.chats.ChatsService;

@RestController
@RequiredArgsConstructor
public class ChatsRestController implements ChatsApi {
    private final ChatsService chatsService;

    @Override
    public ResponseEntity<ChatDto> add(NewOrUpdateChatDto chatDto, String rawToken) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatsService.add(chatDto, rawToken));
    }
}
