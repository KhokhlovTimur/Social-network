package ru.itis.services.chats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.dto.chats.NewOrUpdatePersonalChatDto;
import ru.itis.dto.chats.PersonalChatDto;
import ru.itis.exceptions.AlreadyExistsException;
import ru.itis.exceptions.NotFoundException;
import ru.itis.mappers.chats.ChatsMapper;
import ru.itis.models.ChatGlobalId;
import ru.itis.models.PersonalChat;
import ru.itis.models.User;
import ru.itis.repositories.ChatsGlobalIdsRepository;
import ru.itis.repositories.PersonalChatsRepository;
import ru.itis.services.users.UsersService;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalChatsServiceImpl implements PersonalChatsService {
    private final PersonalChatsRepository personalChatsRepository;
    private final ChatsMapper chatsMapper;
    private final ChatsGlobalIdsRepository chatsGlobalIdsRepository;
    private final UsersService usersService;
    private final UsersServiceUtils usersServiceUtils;


    @Override
    public Set<PersonalChatDto> getBySecondUserUsername(String username, String rawToken) {
        Set<PersonalChatDto> chats = chatsMapper.toPersonalChatSetDto(getByRawToken(rawToken));
        return chats.stream().filter(x -> x.getFirstUser().getUsername().toLowerCase(Locale.ROOT)
                        .contains(username.toLowerCase(Locale.ROOT))
                        || x.getSecondUser().getUsername().toLowerCase(Locale.ROOT)
                        .contains(username.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toSet());
    }

    @Override
    public PersonalChatDto add(NewOrUpdatePersonalChatDto personalChatDto, String rawToken) {
        PersonalChat chat = chatsMapper.toPersonalChat(personalChatDto);
        if (personalChatsRepository.findAllBySecondUserIdOrFirstUserId(personalChatDto.getSecondUserId()).size() == 0 &&
                personalChatsRepository.findAllBySecondUserIdOrFirstUserId(personalChatDto.getFirstUserId()).size() == 0) {
            chat.setFirstUser(usersServiceUtils.getUserFromToken(rawToken));
            chat.setSecondUser(usersService.findById(personalChatDto.getSecondUserId()));

            chat.setGlobalId(chatsGlobalIdsRepository.save(ChatGlobalId.builder()
                    .chatType(ChatGlobalId.ChatType.PERSONAL)
                    .build()));
        }
        else {
            throw new AlreadyExistsException("Chat with this users already exists");
        }

        return chatsMapper.toPersonalChatDto(personalChatsRepository.save(chat));
    }

    @Override
    public PersonalChat getByGlobalId(ChatGlobalId globalId) {
        return personalChatsRepository.findByGlobalId(globalId)
                .orElseThrow(() -> new NotFoundException("Chat with global id <" + globalId + "> not found"));
    }

    @Override
    public Set<PersonalChatDto> getByToken(String token) {
        User user = usersServiceUtils.getUserFromToken(token);
        Set<PersonalChat> chats = personalChatsRepository.findAllBySecondUserIdOrFirstUserId(user.getId());

        return chatsMapper.toPersonalChatSetDto(switchUsers(chats, user));
    }

    private Set<PersonalChat> getByRawToken(String token) {
        User user = usersServiceUtils.getUserFromToken(token);
        Set<PersonalChat> chats = personalChatsRepository.findAllBySecondUserIdOrFirstUserId(user.getId());

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
