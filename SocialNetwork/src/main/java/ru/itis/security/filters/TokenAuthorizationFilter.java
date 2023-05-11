package ru.itis.security.filters;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.itis.repositories.tokens.TokensRepository;
import ru.itis.security.configs.SecurityConfig;
import ru.itis.security.utils.RequestParsingUtil;
import ru.itis.security.utils.JwtUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenAuthorizationFilter extends OncePerRequestFilter {
    public static final String AUTHENTICATION_PATH = "/api/auth/token";
    private final RequestParsingUtil requestParsingUtil;
    private final JwtUtil jwtUtil;
    private final TokensRepository tokensRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals(AUTHENTICATION_PATH) || request.getServletPath().equals(SecurityConfig.PAGES_AUTH_URL)) {
            filterChain.doFilter(request, response);
        } else {

            if (requestParsingUtil.hasAuthorizationTokenInCookie(request) ||
                    requestParsingUtil.hasAuthorizationTokenInHeader(request)) {

                String token = "";
                if (requestParsingUtil.hasAuthorizationTokenInCookie(request)) {
                    token = requestParsingUtil.getTokenFromCookie(request);
                } else {
                    token = requestParsingUtil.getTokenFromHeader(request);
                }

                if (!tokensRepository.isAccessTokenInBlackList(token) && !tokensRepository.isRefreshTokenExists(token)) {
                    try {
                        Authentication authentication = jwtUtil.buildAuthentication(token);

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        filterChain.doFilter(request, response);
                    } catch (JWTVerificationException e) {
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                    }
                } else {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }

}