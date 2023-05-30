package ru.itis.services.users;

import ru.itis.dto.user.FriendResponseDto;
import ru.itis.dto.user.UserFriendResponseDto;
import ru.itis.dto.user.UsersPage;
import ru.itis.models.FriendRequest;

import java.util.Set;

public interface FriendsService {
    FriendResponseDto sendFriendRequest(String username, String friendUsername);

    UsersPage getRequestsOrFriends(String username, String type, String query, int pageNumber);

    UsersPage getFriendsByToken(String token, String type, String query, int pageNumber);

    boolean isFriends(String firstUsername, String secondUsername, String state);

    FriendRequest getByUsernamesAndState(String firstUsername, String secondUsername, String state);

    String getStateByUsernames(String firstUsername, String secondUsername);

    Set<UserFriendResponseDto> addStateToRelations(Set<UserFriendResponseDto> users, String username);

    UsersPage findAllExcludeByUsername(String username, String query, int pageNumber);

    void deleteFriendOrRevokeRequest(String firstUsername, String secondUsername);

    String getStatusNameByTokenAndUsername(String token, String username);

    UsersPage getAllFriendsInChat(String username, Long id);
}
