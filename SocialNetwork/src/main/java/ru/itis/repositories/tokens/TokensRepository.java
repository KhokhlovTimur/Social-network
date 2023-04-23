package ru.itis.repositories.tokens;

import java.util.Set;

public interface TokensRepository {
    void addAccessToken(String token);

    void addRefreshToken(String token);

    boolean isAccessTokenInBlackList(String token);

    boolean isRefreshTokenExists(String token);

    Set<String> getAll();
}
