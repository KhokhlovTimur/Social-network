package ru.itis.controllers.rest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.chats.ChatDto;
import ru.itis.dto.chats.ChatDtoModel;
import ru.itis.dto.chats.NewOrUpdateChatDto;

import java.util.Set;

@RequestMapping("/api/chats")
public interface ChatsApi {

    @PostMapping
    ResponseEntity<ChatDto> add(@RequestBody NewOrUpdateChatDto chatDto,
                                @RequestHeader("Authorization") String rawToken);

    @GetMapping("/{id}")
    ResponseEntity<? extends ChatDtoModel> get(@PathVariable("id") Long id,
                                               @RequestHeader("Authorization") String rawToken);

    @GetMapping
    ResponseEntity<Set<? extends ChatDtoModel>> getByName(@RequestParam("name") String name,
                                               @RequestHeader("Authorization") String rawToken);

    @GetMapping("/all")
    ResponseEntity<Set<? extends ChatDtoModel>> getAll(@RequestHeader("Authorization") String rawToken);
}
