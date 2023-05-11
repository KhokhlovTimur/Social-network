package ru.itis.services.utils;

import ru.itis.models.User;

public interface UsersServiceUtils {
    User getUserFromContext();

    User getUserFromToken(String token);
}
