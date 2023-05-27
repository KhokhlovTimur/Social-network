package ru.itis.services.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.itis.dto.user.UserFriendResponseDto;
import ru.itis.exceptions.NotFoundException;
import ru.itis.models.User;
import ru.itis.repositories.UsersRepository;
import ru.itis.security.details.UserDetailsImpl;
import ru.itis.security.utils.JwtUtil;
import ru.itis.security.utils.RequestParsingUtil;
import ru.itis.security.utils.RequestParsingUtilImpl;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class UsersServiceUtilsImpl implements UsersServiceUtils {
    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;
    private final RequestParsingUtil requestParsingUtil;

    @Override
    public User getUserFromToken(String token) {
        String username;
        if (token.startsWith(RequestParsingUtilImpl.BEARER)) {
            username = requestParsingUtil.getDataFromToken(token).get("username");
        } else {
            username = jwtUtil.parse(token).get("username");
        }

        User user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username \"" + username + "\" not found"));

        return user;
    }
}
