package ru.itis.services.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.dto.user.FriendRequestDto;
import ru.itis.dto.user.PublicUserDto;
import ru.itis.mappers.users.UsersCollectionsMapper;
import ru.itis.models.FriendRequest;
import ru.itis.models.User;
import ru.itis.repositories.FriendsRepository;
import ru.itis.security.utils.JwtUtil;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
    private final FriendsRepository friendsRepository;
    private final UsersService usersService;
    private final JwtUtil jwtUtil;
    private final UsersCollectionsMapper usersCollectionsMapper;
    private final UsersServiceUtils usersServiceUtils;

    @Override
    public void sendFriendRequest(FriendRequestDto requestDto) {
        FriendRequest request = FriendRequest.builder()
                .firstUser(usersService.findById(requestDto.getFirstUserId()))
                .secondUser(usersService.findById(requestDto.getSecondUserId()))
//                .state(FriendRequest.Status.FIRST_WAIT)
                .build();

        friendsRepository.save(request);
    }

    @Override
    public List<PublicUserDto> getFriends(String token) {
        User user = usersServiceUtils.getUserFromToken(token);
        List<User> users = new ArrayList<>();
        List<FriendRequest> friends = friendsRepository.findAllByUserId(user.getId());
        friends.forEach(x -> {
            if (!x.getFirstUser().getId().equals(user.getId())) {
                users.add(x.getFirstUser());
            } else if (!x.getSecondUser().getId().equals(user.getId())) {
                users.add(x.getSecondUser());
            }
        });
        return usersCollectionsMapper.toPublicUsersDtoList(users);
    }

}
