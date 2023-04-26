package ru.itis.services.chats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.dto.chats.ChatDto;
import ru.itis.dto.chats.NewOrUpdateChatDto;
import ru.itis.mappers.chats.ChatsMapper;
import ru.itis.models.Chat;
import ru.itis.models.ChatGlobalId;
import ru.itis.repositories.ChatsGlobalIdsRepository;
import ru.itis.repositories.ChatsRepository;
import ru.itis.repositories.UsersRepository;
import ru.itis.security.utils.AuthorizationsHeaderUtil;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatsServiceImpl implements ChatsService {
    private final ChatsRepository chatsRepository;
    private final ChatsMapper chatsMapper;
    private final UsersRepository usersRepository;
    private final AuthorizationsHeaderUtil authorizationsHeaderUtil;
    private final ChatsGlobalIdsRepository chatsGlobalIdsRepository;

    @Override
    public ChatDto add(NewOrUpdateChatDto chatDto, String rawToken) {
        Chat chat = chatsMapper.toChat(chatDto);
        chat.setDateOfCreation(new Date());
        chat.setOwner(usersRepository.findByUsername(authorizationsHeaderUtil
                .getDataFromToken(rawToken).get("username")).orElseThrow());

        chat.setGlobalId(chatsGlobalIdsRepository.save(ChatGlobalId.builder()
                .chatType(ChatGlobalId.ChatType.PUBLIC)
                .build()));

        return chatsMapper.toDto(chatsRepository.save(chat));
    }
}
