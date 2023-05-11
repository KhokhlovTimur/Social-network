package ru.itis.dto.chats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.itis.dto.messages.MessageDto;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class ChatDtoModel {
    private ChatGlobalIdDto globalId;
}
