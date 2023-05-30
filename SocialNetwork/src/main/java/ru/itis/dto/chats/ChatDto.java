package ru.itis.dto.chats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.itis.dto.user.PublicUserDto;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class ChatDto extends ChatDtoModel {
    private String name;
    private PublicUserDto owner;
    private String imageLink;
    private Date dateOfCreation;
    private int membersCount;
}