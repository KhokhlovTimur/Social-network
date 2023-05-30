package ru.itis.services.chats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.dto.chats.PersonalChatDto;
import ru.itis.exceptions.AlreadyExistsException;
import ru.itis.exceptions.NotFoundException;
import ru.itis.mappers.chats.ChatsMapper;
import ru.itis.models.ChatGlobalId;
import ru.itis.models.Message;
import ru.itis.models.PersonalChat;
import ru.itis.models.User;
import ru.itis.repositories.ChatsGlobalIdsRepository;
import ru.itis.repositories.MessagesRepository;
import ru.itis.repositories.PersonalChatsRepository;
import ru.itis.services.users.UsersService;
import ru.itis.services.utils.ChatsServiceUtils;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalChatsServiceImpl implements PersonalChatsService {
    private final PersonalChatsRepository personalChatsRepository;
    private final ChatsMapper chatsMapper;
    private final ChatsGlobalIdsRepository chatsGlobalIdsRepository;
    private final UsersService usersService;
    private final UsersServiceUtils usersServiceUtils;
    private final MessagesRepository messagesRepository;
    private final ChatsServiceUtils chatServiceUtils;


    @Override
    public PersonalChatDto getByTokenAndUsername(String token, String secondUsername) {
        return chatsMapper.toPersonalChatDto(personalChatsRepository
                .findByFirstUsernameAndSecondUsername(usersServiceUtils.getUserFromToken(token).getUsername(), secondUsername)
                .orElseThrow(() -> new NotFoundException("Chat not found")));
    }

    @Override
    public Set<PersonalChatDto> getAllBySecondUserUsernameLike(String username, String rawToken) {
        Set<PersonalChatDto> chats = chatsMapper.toPersonalChatSetDto(getByRawToken(rawToken));
        return chats.stream().filter(x -> x.getSecondUser().getUsername().toLowerCase(Locale.ROOT)
                        .contains(username.toLowerCase(Locale.ROOT))
                        || x.getSecondUser().getName().toLowerCase(Locale.ROOT).contains(username.toLowerCase(Locale.ROOT))
                        || x.getSecondUser().getSurname().toLowerCase(Locale.ROOT).contains(username.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toSet());
    }

    @Override
    public PersonalChatDto add(String username, String rawToken) {
        Optional<PersonalChat> personalChat = personalChatsRepository.findByFirstUsernameAndSecondUsername(username,
                usersServiceUtils.getUserFromToken(rawToken).getUsername());
        if (personalChat.isEmpty()) {
            PersonalChat chat = PersonalChat.builder()
                    .firstUser(usersServiceUtils.getUserFromToken(rawToken))
                    .secondUser(usersService.findByUsername(username))
                    .build();

            ChatGlobalId chatGlobalId = chatsGlobalIdsRepository.save(ChatGlobalId.builder()
                    .chatType(ChatGlobalId.ChatType.PERSONAL)
                    .build());

            Message message = chatServiceUtils.createChatMessage(chatGlobalId, "Chat was created", Message.MessageType.JOIN, null);
            chatGlobalId.setLastMessage(message);
            chat.setGlobalId(chatGlobalId);
            messagesRepository.save(message);

            return chatsMapper.toPersonalChatDto(personalChatsRepository.save(chat));
        } else {
            throw new AlreadyExistsException("Chat with this users already exists");
        }
    }

    @Override
    public PersonalChat getByGlobalId(ChatGlobalId globalId) {
        return personalChatsRepository.findByGlobalId(globalId)
                .orElseThrow(() -> new NotFoundException("Chat with global id <" + globalId + "> not found"));
    }

    @Override
    public Set<PersonalChatDto> getAllDtoByToken(String token) {
        return chatsMapper.toPersonalChatSetDto(getByRawToken(token));
    }

    private Set<PersonalChat> getByRawToken(String token) {
        User user = usersServiceUtils.getUserFromToken(token);
        Set<PersonalChat> chats = personalChatsRepository.findAllByUsername(user.getUsername());

        return switchUsers(chats, user);
    }

    private Set<PersonalChat> switchUsers(Set<PersonalChat> chats, User user) {
        chats.forEach(x -> {
            if (!Objects.equals(x.getFirstUser().getId(), user.getId())) {
                User user2 = x.getFirstUser();
                x.setFirstUser(user);
                x.setSecondUser(user2);
            }
        });

        return chats;
    }

}
