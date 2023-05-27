package ru.itis.services.utils;

import ru.itis.models.User;

public interface UsersServiceUtils {
    User getUserFromToken(String token);

}
