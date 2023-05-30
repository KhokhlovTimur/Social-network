package ru.itis.security.utils;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.itis.models.User;
import ru.itis.repositories.tokens.TokensRepository;
import ru.itis.security.configs.SecurityConfig;
import ru.itis.security.details.UserDetailsImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static ru.itis.security.configs.SecurityConfig.APP_PREFIX;
import static ru.itis.security.filters.AppAuthorizationFilter.REDIRECT_URL;
import static ru.itis.security.utils.RequestParsingUtilImpl.AUTHORIZATION_COOKIE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationUtilsImpl implements AuthenticationUtils {
    private final TokensRepository tokensRepository;
    private final JwtUtil jwtUtil;

    public final static String USERNAME_PARAMETER = "username";
    private final static String ROLE_PARAMETER = "role";
    @Value("${access-token.expires-time}")
    private int ACCESS_TOKEN_EXPIRES_TIME;

    @Override
    public Authentication buildAuthentication(String token) throws JWTVerificationException {

        if (!tokensRepository.isAccessTokenInBlackList(token) || tokensRepository.isRefreshTokenExists(token)) {
            Map<String, String> data = jwtUtil.parse(token);
            UserDetails userDetails = new UserDetailsImpl(
                    User.builder()
                            .role(User.Role.valueOf(data.get(ROLE_PARAMETER)))
                            .username(data.get(USERNAME_PARAMETER))
                            .build()
            );
            return new UsernamePasswordAuthenticationToken(userDetails, null,
                    Collections.singleton(new SimpleGrantedAuthority(data.get(ROLE_PARAMETER))));
        } else {
            jwtUtil.parse("");
        }

        return null;
    }

    @Override
    public void deleteCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(AUTHORIZATION_COOKIE, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath(APP_PREFIX);
        response.addCookie(cookie);
    }

    @Override
    public Cookie generateSecureCookie(String token) {
        Cookie accessToken = new Cookie(AUTHORIZATION_COOKIE, token);
        accessToken.setHttpOnly(true);
        accessToken.setMaxAge(ACCESS_TOKEN_EXPIRES_TIME / 1000);
        accessToken.setPath(APP_PREFIX);

        return accessToken;
    }

    @Override
    public void setAuthentication(String token, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, boolean isAuthPage) throws ServletException, IOException {
        if (!tokensRepository.isAccessTokenInBlackList(token) && !tokensRepository.isRefreshTokenExists(token)) {
            try {
                Authentication authentication = buildAuthentication(token);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                if (isAuthPage) {
                    response.sendRedirect(REDIRECT_URL);
                } else {
                    filterChain.doFilter(request, response);
                }
            } catch (JWTVerificationException e) {
                log.error(e.getMessage());
                deleteCookie(AUTHORIZATION_COOKIE, response);
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
        } else {
            if (isAuthPage) {
                filterChain.doFilter(request, response);
            } else {
                deleteCookie(AUTHORIZATION_COOKIE, response);
                response.sendRedirect(SecurityConfig.PAGES_AUTH_PATH);
            }
        }
    }
}
