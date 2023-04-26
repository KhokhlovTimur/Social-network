package ru.itis.services.chats;

import ru.itis.dto.other.ChatGlobalIdDto;

public interface ChatsGlobalIdsService {
    ChatGlobalIdDto findById(Long id);
}
