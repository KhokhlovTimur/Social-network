package ru.itis.mappers.chats;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.itis.dto.chats.ChatGlobalIdDto;
import ru.itis.dto.messages.MessageDto;
import ru.itis.dto.messages.NewMessageDto;
import ru.itis.models.ChatGlobalId;
import ru.itis.models.Message;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessagesMapper {
    Message toMessage(NewMessageDto messageDto);

    MessageDto toDto(Message message);

    @Named("withoutMessage")
    @Mapping(target = "lastMessage", ignore = true)
    ChatGlobalIdDto toGlobalIdDto(ChatGlobalId globalId);

    List<MessageDto> toDtoList(List<Message> messages);

}
