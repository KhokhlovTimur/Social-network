package ru.itis.services.users;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.itis.dto.user.ChatFriendResponseDto;
import ru.itis.dto.user.FriendResponseDto;
import ru.itis.dto.user.UserFriendResponseDto;
import ru.itis.dto.user.UsersPage;
import ru.itis.exceptions.AlreadyExistsException;
import ru.itis.exceptions.NotFoundException;
import ru.itis.mappers.users.UsersCollectionsMapper;
import ru.itis.mappers.users.UsersMapper;
import ru.itis.models.FriendRequest;
import ru.itis.models.User;
import ru.itis.repositories.FriendsRepository;
import ru.itis.repositories.UsersRepository;
import ru.itis.services.chats.ChatsGlobalIdsService;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
    private final FriendsRepository friendsRepository;
    private final UsersService usersService;
    private final UsersRepository usersRepository;
    private final UsersCollectionsMapper usersCollectionsMapper;
    private final UsersServiceUtils usersServiceUtils;
    private final UsersMapper usersMapper;
    private final ChatsGlobalIdsService chatsGlobalIdsService;


    @Value("${default.posts-page-size}")
    private int pageSize;

    @Override
    public UsersPage getFriendsByToken(String token, String type, String query, int pageNumber) {
        return getRequestsOrFriends(usersServiceUtils.getUserFromToken(token).getUsername(), type, query, pageNumber);
    }

    @Override
    public boolean isFriends(String firstUsername, String secondUsername, String state) {
        return friendsRepository.findByFirstUserUsernameAndSecondUserUsernameAndState(firstUsername, secondUsername, state).isPresent();
    }

    @Override
    public FriendRequest getByUsernamesAndState(String firstUsername, String secondUsername, String state) {
        return friendsRepository.findByFirstUserUsernameAndSecondUserUsernameAndState(firstUsername, secondUsername, state)
                .orElseThrow(() -> new NotFoundException("Not found friends \"" + firstUsername + "\" and \"" + secondUsername + "\""));
    }

    @Override
    public UsersPage getRequestsOrFriends(String username, String type, String query, int pageNumber) {
        return getFriendsOrRequestsList(username, type, pageNumber, query);
    }

    @Override
    public FriendResponseDto sendFriendRequest(String username, String friendUsername) {
        if (!username.equals(friendUsername) && friendsRepository.findByFirstUserUsernameAndSecondUserUsername(username, friendUsername).isEmpty()) {

            FriendRequest request = FriendRequest.builder()
                    .firstUser(usersService.findByUsername(username))
                    .secondUser(usersService.findByUsername(friendUsername))
                    .state(FriendRequest.Status.FIRST_WAIT.getState())
                    .build();
            friendsRepository.save(request);
            return usersMapper.toFriendResponseDto(request);

        } else if (isFriends(username, friendUsername, FriendRequest.Status.FIRST_WAIT.getState())
                || isFriends(username, friendUsername, FriendRequest.Status.SECOND_WAIT.getState())) {

            FriendRequest friendRequest = friendsRepository.findByFirstUserUsernameAndSecondUserUsername(username, friendUsername).get();

            if ((friendRequest.getState().equals(FriendRequest.Status.FIRST_WAIT.getState())
                    && !friendRequest.getFirstUser().getUsername().equals(username))
                    || (friendRequest.getState().equals(FriendRequest.Status.SECOND_WAIT.getState())
                    && !friendRequest.getSecondUser().getUsername().equals(username))) {

                friendRequest.setState(FriendRequest.Status.ACCEPTED.getState());
                friendsRepository.save(friendRequest);
                return usersMapper.toFriendResponseDto(friendRequest);
            } else {
                throw new AlreadyExistsException("Request was already sent");
            }

        } else {
            throw new AlreadyExistsException("Already friends");
        }
    }

    @Override
    public UsersPage findAllExcludeByUsername(String username, String query, int pageNumber) {
        usersService.findByUsername(username);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<User> page = usersRepository.getAllByUsernameNotAndUsernameLikeOrNameLikeOrSurnameLike(username, query, pageRequest);

        return UsersPage.builder()
                .users(addStateToRelations(usersCollectionsMapper.toFriendResponseDtoSet(page.getContent()), username))
                .totalCount(page.getTotalElements())
                .pagesCount(page.getTotalPages())
                .build();
    }

    @Override
    public void deleteFriendOrRevokeRequest(String firstUsername, String secondUsername) {
        if (friendsRepository.findByFirstUserUsernameAndSecondUserUsername(firstUsername, secondUsername).isPresent()) {

            FriendRequest friendRequest = friendsRepository.findByFirstUserUsernameAndSecondUserUsername(firstUsername, secondUsername).get();

            if (friendRequest.getState().equals(FriendRequest.Status.ACCEPTED.getState())
                    || (friendRequest.getState().equals(FriendRequest.Status.FIRST_WAIT.getState())
                    && friendRequest.getFirstUser().getUsername().equals(firstUsername))
                    || (friendRequest.getState().equals(FriendRequest.Status.SECOND_WAIT.getState())
                    && friendRequest.getSecondUser().getUsername().equals(firstUsername))) {

                friendsRepository.delete(friendRequest);
            } else {
                throw new AlreadyExistsException("User \"" + secondUsername + "\" is already not in friends");
            }
        } else {
            throw new AlreadyExistsException("User \"" + secondUsername + "\" is already not in friends");
        }
    }

    private UsersPage getFriendsOrRequestsList(String username, String type, int pageNumber, String query) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        query = query.toLowerCase(Locale.ROOT);
        Page<User> page;

        switch (type) {
            case "incoming":
                page = usersRepository.getIncomingFriendsRequests(username, query, pageRequest);
                break;
            case "outgoing":
                page = usersRepository.getOutgoingFriendsRequests(username, query, pageRequest);
                break;
            case "friends":
                return getFriends(username, query, pageNumber);
            case "global":
                return findAllExcludeByUsername(username, query, pageNumber);
            default:
                return UsersPage.builder()
                        .users(new HashSet<>())
                        .totalCount(0)
                        .pagesCount(0)
                        .build();
        }

        return UsersPage.builder()
                .users(addStateToRelations(usersCollectionsMapper.toFriendResponseDtoSet(page.getContent()), username))
                .pagesCount(page.getTotalPages())
                .totalCount(page.getTotalElements())
                .build();
    }

    private UsersPage getFriends(String username, String query, int pageNumber) {
        User user = usersService.findByUsername(username);
        List<User> users = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<FriendRequest> requests = friendsRepository.findAllByUserIdAndUsernameLike(user.getId(), query, pageRequest);

        requests.getContent().forEach(x -> {
            if (!x.getFirstUser().getId().equals(user.getId())) {
                users.add(x.getFirstUser());
            } else if (!x.getSecondUser().getId().equals(user.getId())) {
                users.add(x.getSecondUser());
            }
        });
        return UsersPage.builder()
                .users(addStateToRelations(usersCollectionsMapper.toFriendResponseDtoSet(users), username))
                .totalCount(requests.getTotalElements())
                .pagesCount(requests.getTotalPages())
                .build();
    }

    @Override
    public String getStateByUsernames(String firstUsername, String secondUsername) {
        if (friendsRepository.findByFirstUserUsernameAndSecondUserUsername(firstUsername, secondUsername).isPresent()) {
            FriendRequest friendRequest = friendsRepository.findByFirstUserUsernameAndSecondUserUsername(firstUsername, secondUsername).get();

            if (friendRequest.getState().equals(FriendRequest.Status.ACCEPTED.getState())) {
                return friendRequest.getState();
            } else if (friendRequest.getState().equals(FriendRequest.Status.FIRST_WAIT.getState())) {
                if (friendRequest.getFirstUser().getUsername().equals(firstUsername)) {
                    return FriendRequest.Status.FIRST_WAIT.getState();
                } else if (friendRequest.getSecondUser().getUsername().equals(firstUsername)) {
                    return FriendRequest.Status.SECOND_WAIT.getState();
                }
            }
        } else {
            return FriendRequest.Status.NOT_FRIENDS.getState();
        }

        return FriendRequest.Status.NOT_FRIENDS.getState();
    }

    @Override
    public Set<UserFriendResponseDto> addStateToRelations(Set<UserFriendResponseDto> users, String username) {
        users.forEach(x -> x.setFriendStatus(getStateByUsernames(username, x.getUsername())));
        return users;
    }

    @Override
    public UsersPage getAllFriendsInChat(String username, Long id) {
        List<User> users = new ArrayList<>();
        User user = usersService.findByUsername(username);
        friendsRepository.findAllFriendsById(user.getId()).forEach(x -> {
            if (!x.getFirstUser().getId().equals(user.getId()) && !chatsGlobalIdsService.isUserInChat(id, x.getFirstUser().getId())) {
                users.add(x.getFirstUser());
            } else if (!x.getSecondUser().getId().equals(user.getId()) && !chatsGlobalIdsService.isUserInChat(id, x.getSecondUser().getId())) {
                users.add(x.getSecondUser());
            }
        });

        return UsersPage.builder()
                .pagesCount(1)
                .users(usersCollectionsMapper.toPublicUsersDtoSet(users))
                .totalCount(users.size())
                .build();
    }

    @Override
    public String getStatusNameByTokenAndUsername(String token, String username) {
        String state = getStateByUsernames(usersServiceUtils.getUserFromToken(token).getUsername(), username);
        return switch (state) {
            case "0" -> "Delete";
            case "-1" -> "Revoke";
            case "1" -> "Accept";
            case "2" -> "Add";
            default -> "";
        };
    }
}
