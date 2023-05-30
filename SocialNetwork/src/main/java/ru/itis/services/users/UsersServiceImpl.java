package ru.itis.services.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itis.dto.other.TokensDto;
import ru.itis.dto.user.*;
import ru.itis.exceptions.AlreadyExistsException;
import ru.itis.exceptions.NotFoundException;
import ru.itis.exceptions.WrongPasswordException;
import ru.itis.mappers.users.UsersMapper;
import ru.itis.models.User;
import ru.itis.repositories.UsersRepository;
import ru.itis.security.utils.AuthenticationUtils;
import ru.itis.security.utils.JwtUtil;
import ru.itis.security.utils.RequestParsingUtil;
import ru.itis.services.utils.FilesServiceUtils;
import ru.itis.services.utils.UsersServiceUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final PasswordEncoder passwordEncoder;
    private final UsersServiceUtils usersServiceUtils;
    private final FilesServiceUtils filesServiceUtils;
    private final JwtUtil jwtUtil;
    private final AuthenticationUtils authenticationUtils;
    private final RequestParsingUtil requestParsingUtil;

    @Value("${default.page-size}")
    private int pageSize;

    @Override
    public <T extends PublicUserDto> T getByIdAndToken(Long id, String token) {
        User me = usersServiceUtils.getUserFromToken(token);
        User user = getOrThrow(id);

        if (me.getUsername().equals(user.getUsername()) || me.getRole().equals(User.Role.SUPER_ADMIN)) {
            return (T) usersMapper.toPrivateDto(user);
        }
        return (T) usersMapper.toPublicDto(user);
    }

    @Override
    public boolean isMyProfile(String token, String username) {
        User profile = getOrThrowByUsername(username);
        User me = usersServiceUtils.getUserFromToken(token);
        return Objects.equals(profile.getId(), me.getId());
    }

    @Override
    public boolean isUsernameExists(String username) {
        return usersRepository.existsByUsername(username);
    }

    @Override
    public User findByUsername(String username) {
        return getOrThrowByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return getOrThrow(id);
    }

    @Override
    public PrivateUserDto signUp(UserSignUpDto form) {
        User user = usersMapper.toUser(usersMapper.toDto(form));

        user.setRole(User.Role.AUTHORIZED);
        user.setState(User.State.ACTIVE);
        user.setDateOfRegistration(new Date());
        user.setAvatarLink("/images/default.jpg");
        user.setPassword(passwordEncoder.encode(form.getPassword()));

        return usersMapper.toPrivateDto(usersRepository.save(user));
    }

    @Override
    public void banUser(Long id) {
        User user = getOrThrow(id);
        user.setState(User.State.BANNED);

        usersRepository.save(user);
    }

    @Override
    public UserUpdateResponseDto update(String username, UserUpdateDto userDto, HttpServletResponse response) {
        User user = getOrThrowByUsername(username);

        if (userDto.getNewPassword() != null) {
            if (userDto.getPassword() == null || userDto.getNewPassword().length() < 6
                    || !passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
                throw new WrongPasswordException("Wrong password");
            } else {
                user.setPassword(passwordEncoder.encode(userDto.getNewPassword()));
            }
        }
        if (userDto.getUsername() != null && !userDto.getUsername().equals(user.getUsername())) {

            checkUsername(userDto.getUsername());
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getSurname() != null) {
            user.setSurname(userDto.getSurname());
        }
        if (userDto.getAge() != null) {
            user.setAge(userDto.getAge());
        }
        if (userDto.getBio() != null) {
            user.setBio(userDto.getBio());
        }
        if (userDto.getAvatar() != null) {
            String filename = filesServiceUtils.generatePathToFile(userDto.getAvatar()
            );
            user.setAvatarLink(filename);
        }
        if (userDto.getPhoneNumber() != null) {
            user.setPhoneNumber(userDto.getPhoneNumber());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getGender() != null) {
            user.setGender(userDto.getGender());
        }
        usersRepository.save(user);

        UserUpdateResponseDto userUpdateResponseDto = usersMapper.toUpdateResponseDto(user);
        userUpdateResponseDto.setTokens(updateTokens(user, response));

        return userUpdateResponseDto;
    }

    @Override
    public <T extends PublicUserDto> T getByUsername(String username, String token) {
        User me = usersServiceUtils.getUserFromToken(token);
        User user = getOrThrowByUsername(username);

        if (me.getUsername().equals(user.getUsername()) || me.getRole().equals(User.Role.SUPER_ADMIN)) {
            return (T) usersMapper.toPrivateDto(user);
        }
        return (T) usersMapper.toPublicDto(user);
    }

    private User getOrThrow(Long id) {
        User user = usersRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with id \"" + id + "\" not found");
                    throw new NotFoundException("User with id \"" + id + "\" not found");
                });

        if (user.isBanned()) {
            log.error("User with username \"" + user.getUsername() + "\" is banned");
            throw new NotFoundException("User with id \"" + id + "\" is banned");
        }

        return user;
    }

    private User getOrThrowByUsername(String username) {
        User user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username \"" + username + "\" not found"));

        if (user.isBanned()) {
            log.error("User with username \"" + username + "\" is banned");
            throw new NotFoundException("User with username \"" + username + "\" is banned");
        }

        return user;
    }

    private void checkUsername(String username) {
        if (isUsernameExists(username)) {
            throw new AlreadyExistsException("User with username \"" + username + "\" is already exists ");
        }
    }

    private TokensDto updateTokens(User user, HttpServletResponse response) {
        Map<String, String> tokens = jwtUtil.generateTokens(user.getUsername(), user.getRole().toString(), null);
        response.addCookie(authenticationUtils.generateSecureCookie(tokens.get("accessToken")));
        return TokensDto.builder()
                .accessToken(tokens.get("accessToken"))
                .refreshToken(tokens.get("refreshToken"))
                .build();
    }

}
