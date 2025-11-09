package org.example.backend.config;

import lombok.RequiredArgsConstructor;
import org.example.backend.auth.handler.OAuth2SuccessHandler;
import org.example.backend.auth.service.OAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Configuration
 * OAuth2 Social Login Security Settings
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for REST API
                .csrf(csrf -> csrf.disable())
                
                // Session management policy
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                
                // Disable default login form (use social login only)
                .formLogin(form -> form.disable())
                
                // Disable HTTP Basic authentication
                .httpBasic(basic -> basic.disable())
                
                // Request authorization settings
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no authentication required)
                        .requestMatchers(
                                "/",
                                "/health",
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                
                // OAuth2 login configuration
                .oauth2Login(oauth2 -> oauth2
                        // User info endpoint configuration
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                        // Success handler
                        .successHandler(oAuth2SuccessHandler)
                );
        
        // Frame options for iframe usage
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}

