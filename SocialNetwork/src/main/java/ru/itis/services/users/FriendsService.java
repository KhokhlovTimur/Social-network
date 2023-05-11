package ru.itis.services.users;

import ru.itis.dto.user.FriendRequestDto;
import ru.itis.dto.user.PublicUserDto;

import java.util.List;

public interface FriendsService {
    void sendFriendRequest(FriendRequestDto requestDto);

    List<PublicUserDto> getFriends(String token);
}
