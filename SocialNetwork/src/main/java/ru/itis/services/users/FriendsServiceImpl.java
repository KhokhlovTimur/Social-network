package ru.itis.services.users;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.itis.dto.user.UsersPage;
import ru.itis.mappers.users.UsersCollectionsMapper;
import ru.itis.models.FriendRequest;
import ru.itis.models.User;
import ru.itis.repositories.FriendsRepository;
import ru.itis.repositories.UsersRepository;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
    private final FriendsRepository friendsRepository;
    private final UsersService usersService;
    private final UsersRepository usersRepository;
    private final UsersCollectionsMapper usersCollectionsMapper;
    private final UsersServiceUtils usersServiceUtils;

    @Value("${default.posts-page-size}")
    private int pageSize;

    @Override
    public UsersPage getFriendsByToken(String token, String type, int pageNumber) {
        return getRequestsOrFriends(usersServiceUtils.getUserFromToken(token).getUsername(), type, pageNumber);
    }

    @Override
    public UsersPage getRequestsOrFriends(String username, String type, int pageNumber) {
        return getFriendsOrRequestsList(username, type, pageNumber);
    }

    @Override
    public void sendFriendRequest(String username, String friendUsername) {
        if (!username.equals(friendUsername)) {
            FriendRequest request = FriendRequest.builder()
                    .firstUser(usersService.findByUsername(username))
                    .secondUser(usersService.findByUsername(friendUsername))
                    .state(FriendRequest.Status.FIRST_WAIT.getState())
                    .build();

            friendsRepository.save(request);
        }
    }

    private UsersPage getFriendsOrRequestsList(String username, String type, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<User> page;

        switch (type) {
            case "incoming":
                page = usersRepository.getIncomingFriendsRequests(username, pageRequest);
                break;
            case "outgoing":
                page = usersRepository.getOutgoingFriendsRequests(username, pageRequest);
                break;
            case "friends":
                return getFriends(username, pageNumber);
            case "global":
                return usersService.findAllExcludeByUsername(username, pageNumber);
            default:
                return UsersPage.builder()
                        .users(new HashSet<>())
                        .totalCount(0)
                        .pagesCount(0)
                        .build();
        }

        return UsersPage.builder()
                .users(usersCollectionsMapper.toPublicUsersDtoSet(page.getContent()))
                .pagesCount(page.getTotalPages())
                .totalCount(page.getTotalElements())
                .build();
    }

    private UsersPage getFriends(String username, int pageNumber) {
        User user = usersService.findByUsername(username);
        List<User> users = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<FriendRequest> requests = friendsRepository.findAllByUserId(user.getId(), pageRequest);

        requests.getContent().forEach(x -> {
            if (!x.getFirstUser().getId().equals(user.getId())) {
                users.add(x.getFirstUser());
            } else if (!x.getSecondUser().getId().equals(user.getId())) {
                users.add(x.getSecondUser());
            }
        });
        return UsersPage.builder()
                .users(usersCollectionsMapper.toPublicUsersDtoSet(users))
                .totalCount(requests.getTotalElements())
                .pagesCount(requests.getTotalPages())
                .build();
    }
}
