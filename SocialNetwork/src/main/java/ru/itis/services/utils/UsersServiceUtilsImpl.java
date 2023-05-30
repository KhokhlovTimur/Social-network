package ru.itis.services.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import ru.itis.exceptions.NotFoundException;
import ru.itis.models.User;
import ru.itis.repositories.UsersRepository;
import ru.itis.repositories.tokens.TokensRepository;
import ru.itis.security.utils.JwtUtil;
import ru.itis.security.utils.RequestParsingUtil;
import ru.itis.security.utils.RequestParsingUtilImpl;

@RequiredArgsConstructor
@Service
public class UsersServiceUtilsImpl implements UsersServiceUtils {
    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;
    private final RequestParsingUtil requestParsingUtil;
    private final TokensRepository tokensRepository;

    @Override
    public User getUserFromToken(String token) {
        String username;
        if (token.startsWith(RequestParsingUtilImpl.BEARER)) {
            username = requestParsingUtil.getDataFromToken(token).get("username");
        } else {
            username = jwtUtil.parse(token).get("username");
        }

        User user;
        if (usersRepository.findByUsername(username).isPresent()) {
            user = usersRepository.findByUsername(username).get();
        } else {
            tokensRepository.addAccessToken(token);
            throw new NotFoundException("User with username \"" + username + "\" not found");
        }

        return user;
    }
}
