package ru.itis.services.chats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.dto.chats.ChatDto;
import ru.itis.dto.chats.NewOrUpdateChatDto;
import ru.itis.exceptions.NotFoundException;
import ru.itis.mappers.chats.ChatsMapper;
import ru.itis.models.Chat;
import ru.itis.models.ChatGlobalId;
import ru.itis.models.Message;
import ru.itis.repositories.ChatsGlobalIdsRepository;
import ru.itis.repositories.ChatsRepository;
import ru.itis.repositories.MessagesRepository;
import ru.itis.services.utils.ChatsServiceUtils;
import ru.itis.services.utils.FilesServiceUtils;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.Date;
import java.util.HashSet;
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
    private final MessagesRepository messagesRepository;
    private final ChatsServiceUtils chatServiceUtils;
    private final FilesServiceUtils filesServiceUtils;

    @Override
    public Set<ChatDto> getAllByToken(String token) {
        return addMembersCount(chatsMapper.toChatsDtoSet(usersServiceUtils.getUserFromToken(token)
                .getChats()));
    }

    @Override
    public Set<ChatDto> getByRawToken(String token) {
        return null;
    }

    @Override
    public ChatDto add(NewOrUpdateChatDto chatDto, String rawToken) {
        Chat chat = Chat.builder()
                .name(chatDto.getName())
                .build();
        chat.setDateOfCreation(new Date());
        chat.setOwner(usersServiceUtils.getUserFromToken(rawToken));
        chat = chatsRepository.save(chat);
        chat.setUsers(new HashSet<>());
        chat.getUsers().add(usersServiceUtils.getUserFromToken(rawToken));
        chat.setImageLink(filesServiceUtils.generatePathToFile(chatDto.getImage()));

        ChatGlobalId chatGlobalId = chatsGlobalIdsRepository.save(ChatGlobalId.builder()
                .chatType(ChatGlobalId.ChatType.PUBLIC)
                .build());

        Message message = chatServiceUtils.createChatMessage(chatGlobalId, "Chat was created", Message.MessageType.JOIN, null);
        chatGlobalId.setLastMessage(message);
        chat.setGlobalId(chatGlobalId);
        messagesRepository.save(message);

        return chatsMapper.toDto(chatsRepository.save(chat));
    }

    @Override
    public Set<ChatDto> getAllByNameLike(String name, String rawToken) {
        Set<Chat> chats = usersServiceUtils.getUserFromToken(rawToken).getChats();
        return addMembersCount(chatsMapper.toChatsDtoSet(chats.stream()
                .filter(x -> x.getName().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT))).collect(Collectors.toSet())));
    }

    @Override
    public ChatDto getDtoByGlobalChatId(Long globalId) {
        Chat chat = getByGlobalChatId(chatsGlobalIdsRepository.findById(globalId).orElseThrow(() -> new NotFoundException("Chat not found")));
        ChatDto chatDto = chatsMapper.toDto(chat);
        chatDto.setMembersCount(chatsRepository.getMembersCount(globalId));
        return chatDto;
    }

    @Override
    public Chat getByGlobalChatId(ChatGlobalId globalId) {
        return chatsRepository.findByGlobalId(globalId)
                .orElseThrow(() -> new NotFoundException("Chat with global id <" + globalId.getId() + "> not found"));
    }

    private Set<ChatDto> addMembersCount(Set<ChatDto> chats) {
        chats.forEach(x -> x.setMembersCount(getByGlobalChatId(chatsMapper.toChatGlobalId(x.getGlobalId())).getUsers().size()));
        return chats;
    }

}
