package ru.itis.services.messages;

import ru.itis.dto.messages.MessageDto;
import ru.itis.dto.messages.NewMessageDto;

import java.util.List;

public interface MessagesService {
    MessageDto add(Long chatGlobalId, NewMessageDto messageDto, String token);

    List<MessageDto> findAllMessagesFromChat(Long chatGlobalId);

    MessageDto findLastMessageByGlobalId(Long id);
}
