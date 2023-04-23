package ru.itis.repositories.tokens;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokensRepositoryImpl implements TokensRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${access-token.expires-time}")
    private long ACCESS_TOKEN_EXPIRES_TIME;
    private final String REFRESH_KEY = "refresh";

    @Override
    public boolean isRefreshTokenExists(String token) {
        return Objects.requireNonNull(redisTemplate.opsForList()
                .range(REFRESH_KEY, 0, -1)).contains(token);
    }

    @Override
    public void addRefreshToken(String token) {
        redisTemplate.opsForList().rightPush(REFRESH_KEY, token);
    }

    @Override
    public void addAccessToken(String token) {
        if (!isAccessTokenInBlackList(token)) {
            redisTemplate.opsForValue().set(token, "",
                    ACCESS_TOKEN_EXPIRES_TIME, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean isAccessTokenInBlackList(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

    @Override
    public Set<String> getAll() {
        return redisTemplate.keys("*");
    }

}
