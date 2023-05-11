package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.controllers.rest.api.MessagesApi;
import ru.itis.dto.messages.MessageDto;
import ru.itis.dto.messages.NewMessageDto;
import ru.itis.services.messages.MessagesService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessagesRestController implements MessagesApi {
    private final MessagesService messagesService;

    @Override
    public ResponseEntity<MessageDto> addMessage(Long globalId, NewMessageDto messageDto, String rawToken) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messagesService.add(globalId, messageDto, rawToken));
    }

    @Override
    public ResponseEntity<MessageDto> getLastMessage(Long id) {
        return ResponseEntity.ok(messagesService.findLastMessageByGlobalId(id));
    }

    @Override
    public ResponseEntity<List<MessageDto>> getMessages(Long id) {
        return ResponseEntity.ok(messagesService.findAllMessagesFromChat(id));
    }
}
