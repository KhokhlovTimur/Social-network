package ru.itis.websocket.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.chats.NewOrUpdateChatDto;
import ru.itis.dto.messages.MessageDto;
import ru.itis.dto.messages.NewMessageDto;
import ru.itis.models.Message;
import ru.itis.services.chats.ChatsGlobalIdsService;
import ru.itis.services.chats.ChatsService;
import ru.itis.services.chats.PersonalChatsService;
import ru.itis.services.messages.MessagesService;
import ru.itis.websocket.config.WebSocketConfig;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.Session;

import static java.lang.String.format;
import static ru.itis.security.utils.RequestParsingUtilImpl.AUTHORIZATION_COOKIE;

@Controller
@RequiredArgsConstructor
public class WebSocketChatsController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final MessagesService messagesService;
    private final PersonalChatsService personalChatsService;
    private final ChatsService chatsService;

    @GetMapping("/app/chats")
    public String showChats(Model model, @CookieValue(AUTHORIZATION_COOKIE) String token) {
        model.addAttribute("chats", chatsService.getAllByToken(token));
        model.addAttribute("personalChats", personalChatsService.getAllDtoByToken(token));
        return "chats";
    }

    @PostMapping("/app/chats")
    public String addChat(@ModelAttribute NewOrUpdateChatDto chatDto, @CookieValue(AUTHORIZATION_COOKIE) String rawToken) {
        chatsService.add(chatDto, rawToken);
        return "redirect:/app/chats";
    }

    @MessageMapping("/chats/{id}/send")
    public void sendMessage(@DestinationVariable Long id, @Payload NewMessageDto chatMessage,
                            @Header("token") String token) {

        MessageDto message = messagesService.add(id, chatMessage, token);
        messagingTemplate.convertAndSend(WebSocketConfig.BROKER_ENDPOINT + "/" + id, message);
    }

    @MessageMapping("/chats/{id}/subscribe")
    public void addUser(@DestinationVariable String id, @Payload Message chatMessage) {
        messagingTemplate.convertAndSend(format("/chat-room/%s", id), chatMessage);
    }


    @OnClose
    public void close(Session session, CloseReason closeReason) {
        System.out.println(session.getId() + " - " + closeReason);
    }
}
