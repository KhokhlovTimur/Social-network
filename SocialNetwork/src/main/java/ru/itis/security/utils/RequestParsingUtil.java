package ru.itis.security.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface RequestParsingUtil {
    boolean hasAuthorizationTokenInCookie(HttpServletRequest request);

    boolean hasAuthorizationTokenInHeader(HttpServletRequest request);

    Map<String, String> getDataFromToken(String rawToken);

    String getTokenFromCookie(HttpServletRequest request);

    String getTokenFromHeader(HttpServletRequest request);

}
