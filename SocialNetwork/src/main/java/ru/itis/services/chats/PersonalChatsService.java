package ru.itis.services.chats;

import ru.itis.dto.chats.PersonalChatDto;
import ru.itis.models.ChatGlobalId;
import ru.itis.models.PersonalChat;

import java.util.Set;

public interface PersonalChatsService {
    PersonalChatDto add(String username, String rawToken);

    Set<PersonalChatDto> getAllDtoByToken(String token);

    PersonalChat getByGlobalId(ChatGlobalId globalId);

    Set<PersonalChatDto> getAllBySecondUserUsernameLike(String username, String rawToken);

    PersonalChatDto getByTokenAndUsername(String token, String secondUsername);


}
