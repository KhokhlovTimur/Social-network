package ru.itis.services.utils;

import org.springframework.stereotype.Component;
import ru.itis.models.ChatGlobalId;
import ru.itis.models.Message;
import ru.itis.models.User;

import java.util.Date;

@Component
public class ChatsServiceUtilsImpl implements ChatsServiceUtils {
    @Override
    public Message createChatMessage(ChatGlobalId chatGlobalId, String content, Message.MessageType messageType, User sender) {
        return Message.builder()
                .content(content)
                .type(messageType)
                .sendingTime(new Date())
                .chatGlobalId(chatGlobalId)
                .sender(sender)
                .build();
    }
}
