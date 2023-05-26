package ru.itis.services.chats;

import ru.itis.dto.chats.ChatDtoModel;
import ru.itis.dto.chats.ChatGlobalIdDto;
import ru.itis.models.ChatGlobalId;

import java.util.Set;

public interface ChatsGlobalIdsService {
    ChatGlobalIdDto findDtoById(Long id);

    ChatGlobalId findById(Long id);

    <T extends ChatDtoModel> T getChatByGlobalId(Long id, String rawToken);

    Set<? extends ChatDtoModel> getChatsByName(String name, String rawToken);

    Set<? extends ChatDtoModel> getAllChats(String rawToken);

    boolean isUserInChat(Long chatGlobalId, Long userId);
}
