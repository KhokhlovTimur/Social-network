package ru.itis.services.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import ru.itis.repositories.UsersRepository;
import ru.itis.services.users.FriendsService;
import ru.itis.services.users.UsersService;

@Service
@RequiredArgsConstructor
public class PagesModelsUtilsImpl implements PagesModelsUtils {
    private final UsersRepository usersRepository;
    private final FriendsService friendsService;
    private final UsersService usersService;

    @Override
    public String getViewNameByUsername(String username, Model model, String token) {
        if (usersRepository.findByUsername(username).isPresent()) {
            model.addAttribute("user", usersRepository.findByUsername(username).get());
            model.addAttribute("state", friendsService.getStatusNameByTokenAndUsername(token, username));
            model.addAttribute("isMyProfile", usersService.isMyProfile(token, username));
            return "profile";
        } else {
            model.addAttribute("code", HttpStatus.NOT_FOUND.value());
            model.addAttribute("message", "User with username \"" + username + "\" was not found");
            return "error";
        }
    }
}
