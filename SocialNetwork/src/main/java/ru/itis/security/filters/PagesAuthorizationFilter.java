package ru.itis.security.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.itis.security.configs.SecurityConfig;
import ru.itis.security.utils.JwtUtil;
import ru.itis.security.utils.RequestParsingUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PagesAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RequestParsingUtil requestParsingUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().startsWith(SecurityConfig.APP_PREFIX)
                && !request.getServletPath().equals(SecurityConfig.PAGES_AUTH_URL)) {
            if (requestParsingUtil.hasAuthorizationTokenInCookie(request)
                    && requestParsingUtil.isTokenValid(requestParsingUtil.getTokenFromCookie(request), response)) {
                filterChain.doFilter(request, response);
            } else {
                response.sendRedirect(SecurityConfig.PAGES_AUTH_URL);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
