package ru.itis.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FriendRequestDto {
    private Long firstUserId;
    private Long secondUserId;
}
