package ru.itis.security.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.itis.repositories.tokens.TokensRepository;
import ru.itis.security.configs.SecurityConfig;
import ru.itis.security.utils.AuthenticationUtils;
import ru.itis.security.utils.RequestParsingUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.itis.security.utils.RequestParsingUtilImpl.AUTHORIZATION_COOKIE;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutFilter extends OncePerRequestFilter {

    private final RequestParsingUtil requestParsingUtil;
    private final TokensRepository tokensRepository;
    private final String LOGOUT_URL = SecurityConfig.API_PREFIX + "/logout";
    private final AuthenticationUtils authenticationUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals(LOGOUT_URL)) {

            if (requestParsingUtil.hasAuthorizationTokenInHeader(request)) {
                log.info("Someone try to logout");
                String token = requestParsingUtil.getTokenFromHeader(request);
                tokensRepository.addAccessToken(token);
                authenticationUtils.deleteCookie(AUTHORIZATION_COOKIE, response);
                SecurityContextHolder.clearContext();
            } else {
                response.sendRedirect(SecurityConfig.PAGES_AUTH_PATH);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

}
