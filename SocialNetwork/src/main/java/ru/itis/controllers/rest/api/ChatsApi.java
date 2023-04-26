package ru.itis.controllers.rest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.itis.dto.chats.ChatDto;
import ru.itis.dto.chats.NewOrUpdateChatDto;

@RequestMapping("/api/chats")
public interface ChatsApi {

    @PostMapping
    ResponseEntity<ChatDto> add(@RequestBody NewOrUpdateChatDto chatDto,
                                @RequestHeader("Authorization") String rawToken);
}
