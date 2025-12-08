package demoweb.demo.security;

import demoweb.demo.service.AccountService;
import demoweb.demo.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(AccountService accountService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService((UserDetailsService) accountService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain customerSecurityFilterChain(HttpSecurity httpSecurity, SessionService sessionService) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/home",
                                "/contact",
                                "/login",
                                "/admin/login_tthhn",
                                "/manager/login_tthhn",
                                "/sign-up",
                                "/sign-up/**",
                                "/static/**",
                                "/blog/**",
                                "/products/**",
                                "/categories/**",
                                "/error"
                        ).permitAll()

                        .requestMatchers(
                                "/cart/**",
                                "/orderManage/**",
                                "/profile/**",
                                "/account/**"
                        ).hasRole("Customer")

                        .requestMatchers(
                                "/admin/**"
                        ).hasRole("Admin")

                        .requestMatchers(
                                "/manager/**"
                        ).hasRole("Manager")

                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            if (request.getCookies() != null) {
                                for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                                    if ("JWT_TOKEN".equals(cookie.getName())) {
                                        String token = cookie.getValue();
                                        if (token != null && !token.isEmpty()) {
                                            sessionService.closeSessionByToken(token);
                                        }
                                    }
                                }
                            }
                        })
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JWT_TOKEN", "SESSION_ACTIVE", "JSESSIONID")
                        .permitAll()
                );
        return httpSecurity.build();
    }
}