package ru.itis.services.chats;

import ru.itis.dto.chats.ChatDto;
import ru.itis.dto.chats.NewOrUpdateChatDto;

public interface ChatsService {
    ChatDto add(NewOrUpdateChatDto chatDto, String rawToken);
}
