package ru.itis.services.messages;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.dto.messages.MessageDto;
import ru.itis.dto.messages.NewMessageDto;
import ru.itis.mappers.chats.ChatsMapper;
import ru.itis.mappers.chats.MessagesMapper;
import ru.itis.models.Message;
import ru.itis.repositories.ChatsGlobalIdsRepository;
import ru.itis.repositories.MessagesRepository;
import ru.itis.repositories.UsersRepository;
import ru.itis.security.utils.AuthorizationsHeaderUtil;
import ru.itis.services.chats.ChatsGlobalIdsService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class MessagesServiceImpl implements MessagesService {
    private final MessagesRepository messagesRepository;
    private final MessagesMapper messagesMapper;
    private final UsersRepository usersRepository;
    private final AuthorizationsHeaderUtil authorizationsHeaderUtil;
    private final ChatsGlobalIdsService chatsGlobalIdsService;
    private final ChatsMapper chatsMapper;

    @Override
    public MessageDto add(Long chatGlobalId, NewMessageDto messageDto, String rawToken) {
        Message message = messagesMapper.toMessage(messageDto);
        message.setSender(usersRepository.findByUsername(authorizationsHeaderUtil
                .getDataFromToken(rawToken).get("username")).orElseThrow());
        message.setSendingTime(new Date());
        message.setChatGlobalId(chatsMapper.toChatGlobalId(chatsGlobalIdsService.findById(chatGlobalId)));

        return messagesMapper.toDto(messagesRepository.save(message));
    }
}
