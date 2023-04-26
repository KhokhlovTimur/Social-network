package ru.itis.dto.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.dto.user.PublicUserDto;
import ru.itis.models.Message;
import ru.itis.models.User;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MessageDto {
    private Long id;
    private Message.MessageType type;
    private String content;
    private PublicUserDto sender;
}
