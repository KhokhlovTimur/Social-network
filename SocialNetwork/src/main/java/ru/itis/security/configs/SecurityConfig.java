package ru.itis.security.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.itis.security.filters.*;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsServiceImpl;
    private final AuthenticationProvider authenticationProvider;
    private final String AUTH_COOKIE_NAME = "access_token";
    public final static String APP_PREFIX = "/app";
    public final static String PAGES_AUTH_PATH = APP_PREFIX + "/login";
    public final static String API_PREFIX = "/api";

    private final String authPath = ApiAuthorizationFilter.AUTHENTICATION_PATH;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http, TokenAuthenticationFilter tokenAuthenticationFilter,
                                              ApiAuthorizationFilter apiAuthorizationFilter,
                                              CustomLogoutFilter customLogoutFilter,
                                              AppAuthorizationFilter appAuthorizationFilter) throws Exception {
        http.csrf().disable();
        tokenAuthenticationFilter.setFilterProcessesUrl(authPath);

        http.addFilter(tokenAuthenticationFilter)
                .addFilterBefore(apiAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(appAuthorizationFilter, ApiAuthorizationFilter.class)
                .addFilterAt(customLogoutFilter, LogoutFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeHttpRequests()
                .antMatchers("/swagger-ui/index.html/**").permitAll()
                .antMatchers("/api/admin/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                .antMatchers("/app/admin/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                .antMatchers("/api/auth/token").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                .antMatchers("/api/**").authenticated()
                .antMatchers(PAGES_AUTH_PATH).permitAll()
                .antMatchers("/app/**").authenticated()
                .and()
                .logout()
                .logoutUrl("/api/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout", "POST"))
                .deleteCookies("JSESSIONID", AUTH_COOKIE_NAME)
                .invalidateHttpSession(true);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/resources/static/**")
                .antMatchers("/resources/templates/**");
    }

    @Autowired
    public void daoAuthenticationProvider(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);

        authenticationManagerBuilder.userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(passwordEncoder);
    }
}
