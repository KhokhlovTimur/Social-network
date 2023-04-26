package ru.itis.mappers.chats;

import org.mapstruct.Mapper;
import ru.itis.dto.chats.ChatDto;
import ru.itis.dto.chats.NewOrUpdateChatDto;
import ru.itis.dto.other.ChatGlobalIdDto;
import ru.itis.models.Chat;
import ru.itis.models.ChatGlobalId;

@Mapper(componentModel = "spring")
public interface ChatsMapper {
    Chat toChat(NewOrUpdateChatDto chatDto);

    ChatDto toDto(Chat chat);

    ChatGlobalId toChatGlobalId(ChatGlobalIdDto chatGlobalIdDto);
}
