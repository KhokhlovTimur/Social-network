package ru.itis.services.users;

import ru.itis.dto.user.PublicUserDto;
import ru.itis.dto.user.UsersPage;
import ru.itis.models.User;

import java.util.List;

public interface FriendsService {
    void sendFriendRequest(String username, String friendUsername);

    UsersPage getRequestsOrFriends(String username, String type, int pageNumber);

    UsersPage getFriendsByToken(String token, String type, int pageNumber);
}
