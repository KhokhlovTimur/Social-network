package ru.itis.services.chats;

import ru.itis.dto.chats.ChatDto;
import ru.itis.dto.chats.ChatDtoModel;
import ru.itis.dto.chats.ChatGlobalIdDto;
import ru.itis.dto.user.FriendToChatRequestDto;
import ru.itis.models.ChatGlobalId;

import java.util.List;

public interface ChatsGlobalIdsService {
    ChatGlobalIdDto findDtoById(Long id);

    ChatGlobalId findById(Long id);

    <T extends ChatDtoModel> T getChatByGlobalId(Long id, String rawToken);

    List<? extends ChatDtoModel> getChatsByName(String name, String rawToken);

    List<? extends ChatDtoModel> getAllChats(String rawToken);

    boolean isUserInChat(Long chatGlobalId, Long userId);

    void addUserToChat(Long id, String username);

    ChatDto addUsersToChat(Long id, FriendToChatRequestDto requestDto);
}
