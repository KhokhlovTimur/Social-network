package ru.itis.dto.chats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.dto.messages.MessageDto;
import ru.itis.models.ChatGlobalId;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChatGlobalIdDto {
    private Long id;
    private ChatGlobalId.ChatType chatType;
    private MessageDto lastMessage;
}
