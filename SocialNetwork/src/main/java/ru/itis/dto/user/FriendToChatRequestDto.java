package ru.itis.dto.user;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FriendToChatRequestDto {
    private List<String> usernames;
}
