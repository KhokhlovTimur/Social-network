package ru.itis.controllers.rest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.chats.*;
import ru.itis.dto.user.FriendToChatRequestDto;
import ru.itis.dto.user.UsersPage;

import java.util.List;

@RequestMapping("/api/chats")
public interface ChatsApi {

    @PostMapping
    ResponseEntity<ChatDto> addChat(@RequestBody NewOrUpdateChatDto chatDto,
                                    @RequestHeader("Authorization") String rawToken);

    @PostMapping("/personal/{second_username}")
    ResponseEntity<PersonalChatDto> addPersonalChat(@PathVariable("second_username") String username,
                                                    @RequestHeader("Authorization") String rawToken);

    @GetMapping("/{id}")
    ResponseEntity<? extends ChatDtoModel> getById(@PathVariable("id") Long id,
                                                   @RequestHeader("Authorization") String rawToken);

    @GetMapping
    ResponseEntity<List<? extends ChatDtoModel>> getAllByName(@RequestParam("name") String name,
                                                              @RequestHeader("Authorization") String rawToken);

    @GetMapping("/all")
    ResponseEntity<List<? extends ChatDtoModel>> getAllByToken(@RequestHeader("Authorization") String rawToken);

    @GetMapping("/personal/{second_username}")
    ResponseEntity<? extends ChatDtoModel> getByUsernames(@RequestHeader("Authorization") String rawToken,
                                                          @PathVariable("second_username") String username);

    @PostMapping("/{chat_id}")
    ResponseEntity<ChatDto> addUsersToChat(@PathVariable("chat_id") Long id, @RequestBody FriendToChatRequestDto requestDto);

    @GetMapping("/{id}/{username}/friends")
    ResponseEntity<UsersPage> getAllFriends(@PathVariable("username") String username, @PathVariable("id") Long id);
}
