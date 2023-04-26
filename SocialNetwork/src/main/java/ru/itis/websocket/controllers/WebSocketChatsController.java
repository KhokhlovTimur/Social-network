package ru.itis.websocket.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.itis.dto.messages.NewMessageDto;
import ru.itis.models.Message;
import ru.itis.repositories.UsersRepository;
import ru.itis.security.utils.AuthorizationsHeaderUtil;
import ru.itis.services.messages.MessagesService;
import ru.itis.websocket.config.WebSocketConfig;

import static java.lang.String.format;
import static ru.itis.security.utils.JwtUtilImpl.USERNAME_PARAMETER;

@Controller
@RequiredArgsConstructor
public class WebSocketChatsController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final MessagesService messagesService;
    private final UsersRepository usersRepository;
    private final AuthorizationsHeaderUtil authorizationsHeaderUtil;

    @GetMapping("/chats")
    public String showChats(Model model, @RequestHeader("Authorization") String rawToken) {

        model.addAttribute("chats", usersRepository
                .findByUsername(authorizationsHeaderUtil.getDataFromToken(rawToken).get(USERNAME_PARAMETER))
                .orElseThrow().getChats());

        return "/html/chat.html";
    }

    @MessageMapping("/chats/{id}/send")
    public void sendMessage(@DestinationVariable Long id, @Payload NewMessageDto chatMessage,
                            SimpMessageHeaderAccessor headerAccessor) {

        messagesService.add(id, chatMessage, (String) headerAccessor.getSessionAttributes().get("token"));
        messagingTemplate.convertAndSend(format("/%s/%s", WebSocketConfig.BROKER_ENDPOINT, id), chatMessage);
    }

    @MessageMapping("/chats/{id}/addUser")
    public void addUser(@DestinationVariable String id, @Payload Message chatMessage,
                        SimpMessageHeaderAccessor headerAccessor, @Header("Authorization") String rawToken) {

        headerAccessor.getSessionAttributes().put("token", rawToken);
        messagingTemplate.convertAndSend(format("/chat-room/%s", id), chatMessage);
    }
}
