package ru.itis.services.chats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.dto.chats.ChatDtoModel;
import ru.itis.dto.chats.ChatGlobalIdDto;
import ru.itis.exceptions.NotFoundException;
import ru.itis.mappers.chats.ChatsMapper;
import ru.itis.models.ChatGlobalId;
import ru.itis.models.PersonalChat;
import ru.itis.models.User;
import ru.itis.repositories.ChatsGlobalIdsRepository;
import ru.itis.security.utils.RequestParsingUtilImpl;
import ru.itis.services.users.UsersService;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatsGlobalIdsServiceImpl implements ChatsGlobalIdsService {
    private final ChatsGlobalIdsRepository chatsGlobalIdsRepository;
    private final ChatsMapper chatsMapper;
    private final ChatsService chatsService;
    private final PersonalChatsService personalChatsService;
    private final UsersServiceUtils usersServiceUtils;

    @Override
    public ChatGlobalIdDto findDtoById(Long id) {
        ChatGlobalId chatGlobalId = getOrThrow(id);
        return ChatGlobalIdDto.builder()
                .chatType(chatGlobalId.getChatType())
                .id(chatGlobalId.getId())
                .build();
    }

    @Override
    public ChatGlobalId findById(Long id) {
        return getOrThrow(id);
    }

    @Override
    public Set<? extends ChatDtoModel> getAllChats(String rawToken) {
        User user = usersServiceUtils.getUserFromToken(rawToken);
        Set<ChatDtoModel> chats = new HashSet<>();
        chats.addAll(chatsMapper.toChatsDtoSet(user.getChats()));
        chats.addAll(personalChatsService.getBySecondUserUsername(user.getUsername(), rawToken));
        return chats;
    }

    @Override
    public Set<? extends ChatDtoModel> getChatsByName(String name, String rawToken) {
        Set<ChatDtoModel> chats = new HashSet<>();
        chats.addAll(chatsService.getByNameLike(name, rawToken));
        chats.addAll(personalChatsService.getBySecondUserUsername(name, rawToken));

        return chats;
    }

    @Override
    public <T extends ChatDtoModel> T getChatByGlobalId(Long id, String rawToken) {
        User user = usersServiceUtils.getUserFromToken(rawToken);
        ChatGlobalId.ChatType type = getOrThrow(id).getChatType();
        switch (type) {
            case PUBLIC -> {
                return (T) chatsMapper.toDto(chatsService.getByGlobalChatId(getOrThrow(id)));
            }

            case PERSONAL -> {
                PersonalChat personalChat = switchUsers(personalChatsService.getByGlobalId(getOrThrow(id)), user);
                return (T) chatsMapper.
                        toPersonalChatDto(personalChat);
            }
        }

        return null;
    }

    private ChatGlobalId getOrThrow(Long id) {
        return chatsGlobalIdsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dialogue with id <" + id + "> not found"));
    }

    private PersonalChat switchUsers(PersonalChat personalChat, User user) {
        if (!Objects.equals(user.getId(), personalChat.getFirstUser().getId())) {
            User user2 = personalChat.getFirstUser();
            personalChat.setFirstUser(user);
            personalChat.setSecondUser(user2);
        }
        return personalChat;
    }
}
