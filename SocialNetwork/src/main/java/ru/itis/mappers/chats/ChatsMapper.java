package ru.itis.mappers.chats;

import org.mapstruct.Mapper;
import ru.itis.dto.chats.ChatDto;
import ru.itis.dto.chats.NewOrUpdateChatDto;
import ru.itis.dto.chats.NewOrUpdatePersonalChatDto;
import ru.itis.dto.chats.PersonalChatDto;
import ru.itis.dto.chats.ChatGlobalIdDto;
import ru.itis.models.Chat;
import ru.itis.models.ChatGlobalId;
import ru.itis.models.PersonalChat;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ChatsMapper {
    Chat toChat(NewOrUpdateChatDto chatDto);

    ChatDto toDto(Chat chat);

    ChatGlobalId toChatGlobalId(ChatGlobalIdDto chatGlobalIdDto);

    Set<ChatDto> toChatsDtoSet(Set<Chat> chats);

    PersonalChat toPersonalChat(NewOrUpdatePersonalChatDto personalChatDto);

    PersonalChatDto toPersonalChatDto(PersonalChat personalChat);

    Set<PersonalChatDto> toPersonalChatSetDto(Set<PersonalChat> personalChats);
}
