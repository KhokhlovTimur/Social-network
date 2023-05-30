package ru.itis.security.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.itis.security.configs.SecurityConfig;
import ru.itis.security.utils.AuthenticationUtils;
import ru.itis.security.utils.RequestParsingUtil;
import ru.itis.security.utils.JwtUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.itis.security.configs.SecurityConfig.PAGES_AUTH_PATH;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiAuthorizationFilter extends OncePerRequestFilter {
    public static final String AUTHENTICATION_PATH = "/api/auth/token";
    private final RequestParsingUtil requestParsingUtil;
    private final AuthenticationUtils authenticationUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals(AUTHENTICATION_PATH)
                || request.getServletPath().equals(PAGES_AUTH_PATH)) {
            filterChain.doFilter(request, response);
        } else {
            if (requestParsingUtil.hasAuthorizationTokenInHeader(request) && request.getServletPath().startsWith(SecurityConfig.API_PREFIX)) {
                String token = requestParsingUtil.getTokenFromHeader(request);

                authenticationUtils.setAuthentication(token, request, response, filterChain, false);
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }

}