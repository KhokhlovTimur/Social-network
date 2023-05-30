package ru.itis.security.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.itis.security.configs.SecurityConfig;
import ru.itis.security.utils.AuthenticationUtils;
import ru.itis.security.utils.JwtUtil;
import ru.itis.security.utils.RequestParsingUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RequestParsingUtil requestParsingUtil;
    private final AuthenticationUtils authenticationUtils;
    public static String REDIRECT_URL = SecurityConfig.APP_PREFIX + "/feeds";

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().startsWith(SecurityConfig.APP_PREFIX)
                && !request.getServletPath().equals(SecurityConfig.PAGES_AUTH_PATH)) {

            if (requestParsingUtil.hasAuthorizationTokenInCookie(request)) {
                String token = requestParsingUtil.getTokenFromCookie(request);
                authenticationUtils.setAuthentication(token, request, response, filterChain, false);
            } else {
                response.sendRedirect(SecurityConfig.PAGES_AUTH_PATH);
                log.info("Someone try to get resource, but token is outdated");
            }

        } else if (request.getServletPath().equals(SecurityConfig.PAGES_AUTH_PATH)) {

            if (requestParsingUtil.hasAuthorizationTokenInCookie(request)) {
                String token = requestParsingUtil.getTokenFromCookie(request);
                authenticationUtils.setAuthentication(token, request, response, filterChain, true);
            } else {
                filterChain.doFilter(request, response);
            }

        } else {
            filterChain.doFilter(request, response);
        }
    }
}
