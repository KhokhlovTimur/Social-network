package ru.itis.services.messages;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.dto.chats.ChatDtoModel;
import ru.itis.dto.messages.MessageDto;
import ru.itis.dto.messages.NewMessageDto;
import ru.itis.exceptions.NotFoundException;
import ru.itis.mappers.chats.ChatsMapper;
import ru.itis.mappers.chats.MessagesMapper;
import ru.itis.models.ChatGlobalId;
import ru.itis.models.Message;
import ru.itis.repositories.ChatsGlobalIdsRepository;
import ru.itis.repositories.MessagesRepository;
import ru.itis.repositories.UsersRepository;
import ru.itis.security.utils.JwtUtil;
import ru.itis.services.chats.ChatsGlobalIdsService;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessagesServiceImpl implements MessagesService {
    private final MessagesRepository messagesRepository;
    private final MessagesMapper messagesMapper;
    private final ChatsGlobalIdsService chatsGlobalIdsService;
    private final ChatsGlobalIdsRepository chatsGlobalIdsRepository;
    private final ChatsMapper chatsMapper;
    private final UsersServiceUtils usersServiceUtils;

    @Override
    public MessageDto add(Long chatGlobalId, NewMessageDto messageDto, String token) {
        Message message = messagesMapper.toMessage(messageDto);
        message.setSender(usersServiceUtils.getUserFromToken(token));
        message.setSendingTime(new Date());
        message.setChatGlobalId(chatsMapper.toChatGlobalId(chatsGlobalIdsService.findDtoById(chatGlobalId)));

        ChatGlobalId chat = chatsGlobalIdsService.findById(chatGlobalId);
        chat.setLastMessage(message);
        chatsGlobalIdsRepository.save(chat);
        MessageDto newMessage = messagesMapper.toDto(chat.getLastMessage());
        newMessage.setGlobalId(chatsGlobalIdsService.findDtoById(chatGlobalId));
        return newMessage;
    }

    @Override
    public MessageDto findLastMessageByGlobalId(Long id) {
        Message message = chatsGlobalIdsRepository.findById(id).orElseThrow(
                () -> new NotFoundException("There are no messages in the chat with id \"" + id + "\" ")
        ).getLastMessage();
        return messagesMapper.toDto(message);
    }

    @Override
    public List<MessageDto> findAllMessagesFromChat(Long chatGlobalId) {
        List<MessageDto> messages = messagesMapper
                .toDtoList(messagesRepository.
                        findAllByChatGlobalIdOrderBySendingTime(chatsGlobalIdsService.findById(chatGlobalId)));

        messages.sort(Comparator.comparing(MessageDto::getSendingTime));
        return messages;
    }

}
