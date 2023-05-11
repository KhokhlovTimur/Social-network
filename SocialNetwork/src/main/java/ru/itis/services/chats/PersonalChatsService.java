package ru.itis.services.chats;

import ru.itis.dto.chats.NewOrUpdatePersonalChatDto;
import ru.itis.dto.chats.PersonalChatDto;
import ru.itis.models.ChatGlobalId;
import ru.itis.models.PersonalChat;

import java.util.Set;

public interface PersonalChatsService {
    PersonalChatDto add(NewOrUpdatePersonalChatDto personalChatDto, String rawToken);

    Set<PersonalChatDto> getByToken(String token);

    PersonalChat getByGlobalId(ChatGlobalId globalId);

    Set<PersonalChatDto> getBySecondUserUsername(String username, String rawToken);


}
