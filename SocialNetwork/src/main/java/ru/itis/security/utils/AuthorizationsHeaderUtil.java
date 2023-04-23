package ru.itis.security.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface AuthorizationsHeaderUtil {
    boolean hasAuthorizationToken(HttpServletRequest request);

    Map<String, String> getDataFromToken(String rawToken);

    String getToken(HttpServletRequest request);

    boolean isTokenValid(String token, HttpServletResponse response);
}
