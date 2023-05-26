package ru.itis.services.chats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.dto.chats.ChatDto;
import ru.itis.dto.chats.NewOrUpdateChatDto;
import ru.itis.exceptions.NotFoundException;
import ru.itis.mappers.chats.ChatsMapper;
import ru.itis.models.Chat;
import ru.itis.models.ChatGlobalId;
import ru.itis.repositories.ChatsGlobalIdsRepository;
import ru.itis.repositories.ChatsRepository;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatsServiceImpl implements ChatsService {
    private final ChatsRepository chatsRepository;
    private final ChatsMapper chatsMapper;
    private final UsersServiceUtils usersServiceUtils;
    private final ChatsGlobalIdsRepository chatsGlobalIdsRepository;

    @Override
    public Set<ChatDto> getByToken(String token) {
        return chatsMapper.toChatsDtoSet(usersServiceUtils.getUserFromToken(token)
                .getChats());
    }

    @Override
    public Set<ChatDto> getByRawToken(String token) {
        return null;
    }

    @Override
    public ChatDto add(NewOrUpdateChatDto chatDto, String rawToken) {
        Chat chat = chatsMapper.toChat(chatDto);
        chat.setDateOfCreation(new Date());
        chat.setOwner(usersServiceUtils.getUserFromToken(rawToken));

        chat.setGlobalId(chatsGlobalIdsRepository.save(ChatGlobalId.builder()
                .chatType(ChatGlobalId.ChatType.PUBLIC)
                .build()));

        return chatsMapper.toDto(chatsRepository.save(chat));
    }

    @Override
    public Set<ChatDto> getByNameLike(String name, String rawToken) {
        Set<Chat> chats = usersServiceUtils.getUserFromToken(rawToken).getChats();
        return chatsMapper.toChatsDtoSet(chats.stream()
                .filter(x -> x.getName().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT))).collect(Collectors.toSet()));
    }

    @Override
    public Chat getByGlobalChatId(ChatGlobalId globalId) {
        return chatsRepository.findByGlobalId(globalId)
                .orElseThrow(() -> new NotFoundException("Chat with global id <" + globalId.getId() + "> not found"));
    }

}
