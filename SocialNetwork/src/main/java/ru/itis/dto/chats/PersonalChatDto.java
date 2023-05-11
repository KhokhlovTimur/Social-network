package ru.itis.dto.chats;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.itis.dto.user.PublicUserDto;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class PersonalChatDto extends ChatDtoModel {
//    private Long id;
    private PublicUserDto firstUser;
    private PublicUserDto secondUser;
}
