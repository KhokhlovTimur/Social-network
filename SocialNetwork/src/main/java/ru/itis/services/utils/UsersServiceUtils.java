package ru.itis.services.utils;

import org.springframework.ui.Model;
import ru.itis.models.User;

public interface UsersServiceUtils {
    User getUserFromToken(String token);

}
