package ru.itis.security.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.itis.repositories.tokens.TokensRepository;
import ru.itis.security.utils.RequestParsingUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutFilter extends OncePerRequestFilter {

    private final RequestParsingUtil requestParsingUtil;
    private final TokensRepository tokensRepository;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private final String LOGOUT_URL = "/logout";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals(LOGOUT_URL)) {
            String token = requestParsingUtil.getTokenFromCookie(request);

            if (token != null && requestParsingUtil.isTokenValid(token, response)) {
                tokensRepository.addAccessToken(token);
                SecurityContextHolder.clearContext();
            } else {
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
        }

        filterChain.doFilter(request, response);
    }

}
