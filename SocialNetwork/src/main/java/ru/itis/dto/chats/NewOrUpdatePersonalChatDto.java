package ru.itis.dto.chats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NewOrUpdatePersonalChatDto {
    private ChatGlobalIdDto globalId;
    private String firstUsername;
    private String secondUsername;
}
