package ru.itis.controllers.rest.api;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.messages.MessageDto;
import ru.itis.dto.messages.NewMessageDto;

import java.util.List;

@RequestMapping("/api")
public interface MessagesApi {
    @PostMapping(value = {"/chats/{global_id}/messages", "/personal_chats/{global_id}/messages"})
    ResponseEntity<MessageDto> addMessage(@PathVariable("global_id") Long globalId,
                                          @RequestBody NewMessageDto messageDto,
                                          @RequestHeader("Authorization") String rawToken);

    @GetMapping("/chats/{global_id}/messages")
    ResponseEntity<List<MessageDto>> getMessages(@PathVariable("global_id") Long id);

    @GetMapping("/chats/{global_id}/messages/last")
    ResponseEntity<MessageDto> getLastMessage(@PathVariable("global_id") Long id);
}
