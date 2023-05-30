package ru.itis.security.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.itis.exceptions.NoAccessException;
import ru.itis.security.configs.SecurityConfig;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.apache.tomcat.websocket.Constants.AUTHORIZATION_HEADER_NAME;

@Component
@RequiredArgsConstructor
public class RequestParsingUtilImpl implements RequestParsingUtil {
    public static String BEARER = "Bearer ";
    public static final String AUTHORIZATION_COOKIE = "access_token";
    private final JwtUtil jwtUtil;

    @Override
    public boolean hasAuthorizationTokenInHeader(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER_NAME);
        return header != null && header.startsWith(BEARER);
    }

    @Override
    public String getTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER_NAME);
        return authorizationHeader.substring(BEARER.length());
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
    public boolean hasAuthorizationTokenInCookie(HttpServletRequest request) {
        return getTokenFromCookie(request) != null;
    }


    @Override
    public String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_COOKIE) && cookie.getValue() != null) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }


}
