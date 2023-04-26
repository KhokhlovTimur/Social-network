package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.controllers.rest.api.MessagesApi;
import ru.itis.dto.messages.MessageDto;
import ru.itis.dto.messages.NewMessageDto;
import ru.itis.services.messages.MessagesService;

@RestController
@RequiredArgsConstructor
public class MessagesRestController implements MessagesApi {
    private final MessagesService messagesService;

    @Override
    public ResponseEntity<MessageDto> addMessage(Long globalId, NewMessageDto messageDto, String rawToken) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messagesService.add(globalId, messageDto, rawToken));
    }
}
