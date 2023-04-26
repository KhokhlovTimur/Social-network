package ru.itis.dto.chats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.dto.other.ChatGlobalIdDto;
import ru.itis.dto.user.PublicUserDto;
import ru.itis.models.ChatGlobalId;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChatDto {
//    private Long id;
    private ChatGlobalIdDto globalId;
    private String name;
    private PublicUserDto owner;
    private Date dateOfCreation;
}
