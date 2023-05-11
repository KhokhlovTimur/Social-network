package ru.itis.services.chats;

import ru.itis.dto.chats.ChatDto;
import ru.itis.dto.chats.NewOrUpdateChatDto;
import ru.itis.models.Chat;
import ru.itis.models.ChatGlobalId;

import java.util.Set;

public interface ChatsService {
    ChatDto add(NewOrUpdateChatDto chatDto, String rawToken);

    Chat getByGlobalChatId(ChatGlobalId globalId);

    Set<ChatDto> getByNameLike(String name, String rawToken);

    Set<ChatDto> getByToken(String token);

    Set<ChatDto> getByRawToken(String token);

    //add people
}
