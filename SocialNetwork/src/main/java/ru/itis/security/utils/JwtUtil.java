package ru.itis.security.utils;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.security.core.Authentication;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface JwtUtil {
    Map<String, String> generateTokens(String subject, String authority, String issuer);

    Authentication buildAuthentication(String token) throws JWTVerificationException;

    Map<String, String> parse(String token) throws JWTVerificationException;

    void setAuthentication(String token, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, boolean isAuthPage) throws ServletException, IOException;
}
