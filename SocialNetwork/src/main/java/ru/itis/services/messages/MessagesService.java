package ru.itis.services.messages;

import ru.itis.dto.messages.MessageDto;
import ru.itis.dto.messages.NewMessageDto;

public interface MessagesService {
    MessageDto add(Long chatGlobalId, NewMessageDto messageDto, String rawToken);
}
