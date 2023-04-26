package ru.itis.services.chats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.dto.other.ChatGlobalIdDto;
import ru.itis.exceptions.NotFoundException;
import ru.itis.models.ChatGlobalId;
import ru.itis.repositories.ChatsGlobalIdsRepository;

@Service
@RequiredArgsConstructor
public class ChatsGlobalIdsServiceImpl implements ChatsGlobalIdsService {
    private final ChatsGlobalIdsRepository chatsGlobalIdsRepository;

    @Override
    public ChatGlobalIdDto findById(Long id) {
        ChatGlobalId chatGlobalId = getOrThrow(id);
        return ChatGlobalIdDto.builder()
                .chatType(chatGlobalId.getChatType())
                .id(chatGlobalId.getId())
                .build();
    }

    private ChatGlobalId getOrThrow(Long id){
        return chatsGlobalIdsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dialogue with id <" + id + "> not found"));
    }
}
