package ru.itis.security.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.itis.exceptions.NoAccessException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthorizationHeaderUtilImpl implements AuthorizationsHeaderUtil {
    private static String BEARER = "Bearer ";
    private static String AUTHORIZATION_HEADER = "Authorization";
    @Value("${jwt.secret.key}")
    private String secretKey;
    private final JwtUtil jwtUtil;

    @Override
    public boolean hasAuthorizationToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        return header != null && header.startsWith(BEARER);
    }

    public boolean isTokenValid(String token, HttpServletResponse response) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes(StandardCharsets.UTF_8));
            JWT.require(algorithm).build().verify(token);
        } catch (JWTVerificationException e) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }
        return true;
    }

    @Override
    public Map<String, String> getDataFromToken(String rawToken) {
        if (rawToken.startsWith(BEARER)) {
            try {
                return jwtUtil.parse(rawToken.substring(BEARER.length()));
            } catch (JWTVerificationException e) {
                throw new NoAccessException("No access to the resource");
            }
        } else {
            throw new NoAccessException("No access to the resource");
        }
    }

    @Override
    public String getToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        return header.substring(BEARER.length());
    }
}
