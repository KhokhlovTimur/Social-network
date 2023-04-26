package ru.itis.mappers.chats;

import org.mapstruct.Mapper;
import ru.itis.dto.messages.MessageDto;
import ru.itis.dto.messages.NewMessageDto;
import ru.itis.models.Message;

@Mapper(componentModel = "spring")
public interface MessagesMapper {
    Message toMessage(NewMessageDto messageDto);

    MessageDto toDto(Message message);

}
