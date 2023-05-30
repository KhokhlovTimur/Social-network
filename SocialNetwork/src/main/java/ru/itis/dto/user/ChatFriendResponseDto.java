package ru.itis.dto.user;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class ChatFriendResponseDto extends PublicUserDto {
    private boolean isInChat;
}
