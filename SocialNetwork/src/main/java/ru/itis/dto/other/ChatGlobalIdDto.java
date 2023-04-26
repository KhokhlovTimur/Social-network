package ru.itis.dto.other;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.models.ChatGlobalId;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChatGlobalIdDto {
    private Long id;
    private ChatGlobalId.ChatType chatType;
}
