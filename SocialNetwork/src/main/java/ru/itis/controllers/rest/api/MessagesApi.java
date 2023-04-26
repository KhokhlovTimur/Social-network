package ru.itis.controllers.rest.api;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.itis.dto.messages.MessageDto;
import ru.itis.dto.messages.NewMessageDto;

public interface MessagesApi {
    @PostMapping(value = {"/api/chats/{global_id}/messages", "/api/personal_chats/{global_id}/messages"})
    public ResponseEntity<MessageDto> addMessage(@PathVariable("global_id") Long globalId,
                                                 @RequestBody NewMessageDto messageDto,
                                                 @RequestHeader("Authorization") String rawToken);
}
