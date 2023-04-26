package ru.itis.dto.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.models.Message;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NewMessageDto {
    private Message.MessageType type;
    private String content;
}
