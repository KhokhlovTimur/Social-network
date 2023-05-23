package ru.itis.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import ru.itis.repositories.tokens.TokensRepository;
import ru.itis.security.authentication.RefreshTokenAuthentication;
import ru.itis.security.details.UserDetailsImpl;
import ru.itis.security.utils.JwtUtilImpl;
import ru.itis.security.utils.RequestParsingUtil;
import ru.itis.security.utils.JwtUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


@Slf4j
@Component
public class TokenAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final RequestParsingUtil requestParsingUtil;
    private final JwtUtil jwtUtil;
    private final TokensRepository tokensRepository;

    public TokenAuthenticationFilter(AuthenticationConfiguration authenticationConfiguration,
                                     RequestParsingUtil requestParsingUtil, JwtUtil jwtUtil,
                                     TokensRepository tokensRepository, ObjectMapper objectMapper) throws Exception {
        super(authenticationConfiguration.getAuthenticationManager());
        this.requestParsingUtil = requestParsingUtil;
        this.setUsernameParameter(JwtUtilImpl.USERNAME_PARAMETER);
        this.jwtUtil = jwtUtil;
        this.tokensRepository = tokensRepository;
        this.objectMapper = objectMapper;
    }

    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (requestParsingUtil.hasAuthorizationTokenInHeader(request)) {
            String refreshToken = requestParsingUtil.getTokenFromHeader(request);

            RefreshTokenAuthentication authentication = new RefreshTokenAuthentication(refreshToken);

//            Cookie accessCookieToken = requestParsingUtil.generateSecureCookie(refreshToken);
//            response.addCookie(accessCookieToken);

            return super.getAuthenticationManager().authenticate(authentication);
        } else {
            return super.attemptAuthentication(request, response);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        response.setContentType("application/json");

        GrantedAuthority grantedAuthority = authResult.getAuthorities().stream().findFirst().orElseThrow();
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();

        Map<String, String> tokens = jwtUtil.generateTokens(
                userDetails.getUsername(), grantedAuthority.toString(), request.getRequestURI()
        );

        tokensRepository.addRefreshToken(tokens.get("refreshToken"));

        Cookie accessCookieToken = requestParsingUtil.generateSecureCookie(tokens.get("accessToken"));
        response.addCookie(accessCookieToken);

        objectMapper.writeValue(response.getWriter(), tokens);
    }
}
