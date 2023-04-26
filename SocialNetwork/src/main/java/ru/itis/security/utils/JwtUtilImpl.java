package ru.itis.security.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.itis.models.User;
import ru.itis.repositories.tokens.TokensRepository;
import ru.itis.security.details.UserDetailsImpl;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtilImpl implements JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;
    @Value("${access-token.expires-time}")
    private long ACCESS_TOKEN_EXPIRES_TIME;
    @Value("${refresh-token.expires-time}")
    private long REFRESH_TOKEN_EXPIRES_TIME;
    private final TokensRepository tokensRepository;
    public final static String USERNAME_PARAMETER = "username";
    private final static String ROLE_PARAMETER = "role";

    @Override
    public Map<String, String> generateTokens(String subject, String authority, String issuer) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes(StandardCharsets.UTF_8));

        String accessToken = JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRES_TIME))
                .withClaim(ROLE_PARAMETER, authority)
                .withIssuer(issuer)
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRES_TIME))
                .withClaim(ROLE_PARAMETER, authority)
                .withIssuer(issuer)
                .sign(algorithm);

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    @Override
    public Authentication buildAuthentication(String token) throws JWTVerificationException {

        if (!tokensRepository.isAccessTokenInBlackList(token) || tokensRepository.isRefreshTokenExists(token)) {
            Map<String, String> data = parse(token);
            UserDetails userDetails = new UserDetailsImpl(
                    User.builder()
                            .role(User.Role.valueOf(data.get(ROLE_PARAMETER)))
                            .username(data.get(USERNAME_PARAMETER))
                            .build()
            );
            return new UsernamePasswordAuthenticationToken(userDetails, null,
                    Collections.singleton(new SimpleGrantedAuthority(data.get(ROLE_PARAMETER))));
        } else {
            parse("");
        }

        return null;
    }

    public Map<String, String> parse(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes(StandardCharsets.UTF_8));

        JWTVerifier jwtVerifier = JWT.require(algorithm).build();

        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        Map<String, String> data = new HashMap<>();

        data.put(USERNAME_PARAMETER, decodedJWT.getSubject());
        data.put(ROLE_PARAMETER, decodedJWT.getClaim(ROLE_PARAMETER).asString());
        return data;
    }
}