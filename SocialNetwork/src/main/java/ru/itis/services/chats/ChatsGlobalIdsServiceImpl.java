package ru.itis.services.chats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.dto.chats.ChatDto;
import ru.itis.dto.chats.ChatDtoModel;
import ru.itis.dto.chats.ChatGlobalIdDto;
import ru.itis.dto.user.FriendToChatRequestDto;
import ru.itis.exceptions.AlreadyExistsException;
import ru.itis.exceptions.NoAccessException;
import ru.itis.exceptions.NotFoundException;
import ru.itis.mappers.chats.ChatsMapper;
import ru.itis.models.Chat;
import ru.itis.models.ChatGlobalId;
import ru.itis.models.PersonalChat;
import ru.itis.models.User;
import ru.itis.repositories.ChatsGlobalIdsRepository;
import ru.itis.repositories.ChatsRepository;
import ru.itis.services.users.UsersService;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatsGlobalIdsServiceImpl implements ChatsGlobalIdsService {
    private final ChatsGlobalIdsRepository chatsGlobalIdsRepository;
    private final ChatsMapper chatsMapper;
    private final ChatsService chatsService;
    private final ChatsRepository chatsRepository;
    private final PersonalChatsService personalChatsService;
    private final UsersServiceUtils usersServiceUtils;
    private final UsersService usersService;

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
    public ChatDto addUsersToChat(Long id, FriendToChatRequestDto requestDto) {
        if (requestDto.getUsernames() != null) {
            for (String username : requestDto.getUsernames()) {
                addUserToChat(id, username);
            }
        } else {
            throw new NotFoundException("Can't find usernames");
        }

        return chatsMapper.toDto(chatsService.getByGlobalChatId(getOrThrow(id)));
    }

    @Override
    public void addUserToChat(Long id, String username) {
        ChatGlobalId chatGlobalId = getOrThrow(id);
        if (!isUserInChat(chatGlobalId.getId(), usersService.findByUsername(username).getId())
                && chatGlobalId.getChatType().equals(ChatGlobalId.ChatType.PUBLIC)) {

            Chat chat = chatsService.getByGlobalChatId(chatGlobalId);
            chat.getUsers().add(usersService.findByUsername(username));
            chatsRepository.save(chat);
        } else {
            throw new AlreadyExistsException("User is already in chat");
        }
    }

    @Override
    public boolean isUserInChat(Long chatGlobalId, Long userId) {
        return chatsGlobalIdsRepository.isUserInChat(chatGlobalId, userId);
    }

    @Override
    public List<? extends ChatDtoModel> getAllChats(String rawToken) {
        User user = usersServiceUtils.getUserFromToken(rawToken);
        List<ChatDtoModel> chats = new ArrayList<>();
        chats.addAll(chatsMapper.toChatsDtoSet(user.getChats()));
        chats.addAll(personalChatsService.getAllDtoByToken(rawToken));
        chats.sort(Comparator.comparing(o -> o.getGlobalId().getLastMessage().getSendingTime()));
        return chats;
    }

    @Override
    public List<? extends ChatDtoModel> getChatsByName(String name, String rawToken) {
        List<ChatDtoModel> chats = new ArrayList<>();
        chats.addAll(chatsService.getAllByNameLike(name, rawToken));
        chats.addAll(personalChatsService.getAllBySecondUserUsernameLike(name, rawToken));
        chats.sort(Comparator.comparing(o -> o.getGlobalId().getLastMessage().getSendingTime()));
        return chats;
    }

    @Override
    public <T extends ChatDtoModel> T getChatByGlobalId(Long id, String rawToken) {
        User user = usersServiceUtils.getUserFromToken(rawToken);
        ChatGlobalId.ChatType type = getOrThrow(id).getChatType();
        switch (type) {
            case PUBLIC -> {
                if (isUserInChat(id, user.getId())) {
                    return (T) chatsMapper.toDto(chatsService.getByGlobalChatId(getOrThrow(id)));
                } else {
                    throw new NoAccessException("You are not in the chat");
                }
            }

            case PERSONAL -> {
                if (isUserInChat(id, user.getId())) {
                    PersonalChat personalChat = switchUsers(personalChatsService.getByGlobalId(getOrThrow(id)), user);
                    return (T) chatsMapper.toPersonalChatDto(personalChat);
                } else {
                    throw new NoAccessException("You are not in the chat");
                }
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
