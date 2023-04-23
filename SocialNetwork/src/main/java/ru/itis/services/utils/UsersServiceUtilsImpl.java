package ru.itis.services.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.itis.models.User;
import ru.itis.repositories.UsersRepository;
import ru.itis.security.details.UserDetailsImpl;

@RequiredArgsConstructor
@Service
public class UsersServiceUtilsImpl implements UsersServiceUtils {
    private final UsersRepository usersRepository;

    public User getUserFromContext() {
        String username = ((UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getUsername();

        return usersRepository.findByUsername(username).orElseThrow();
    }
}
