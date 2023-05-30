package ru.itis.security.utils;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.security.core.Authentication;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthenticationUtils {

    void setAuthentication(String token, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, boolean isAuthPage) throws ServletException, IOException;

    Authentication buildAuthentication(String token) throws JWTVerificationException;

    Cookie generateSecureCookie(String token);

    void deleteCookie(String name, HttpServletResponse response);
}
