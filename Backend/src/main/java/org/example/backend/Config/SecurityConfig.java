package org.example.backend.config;

import lombok.RequiredArgsConstructor;
import org.example.backend.auth.filter.JwtAuthenticationFilter;
import org.example.backend.auth.handler.OAuth2AuthenticationEntryPoint;
import org.example.backend.auth.handler.OAuth2SuccessHandler;
import org.example.backend.auth.service.OAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
    private final OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Completely ignore /login so Spring Security does not try to generate its default login page.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Let Spring Security filter chain manage OAuth endpoints (no ignores)
        return web -> {};
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS for frontend (8081)
                .cors(cors -> {})
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
                
                // Add JWT authentication filter before OAuth2 login filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                
                // Request authorization settings
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no authentication required)
                        .requestMatchers(
                                "/",
                                "/health",
                                "/oauth/**",  // Custom OAuth endpoints: /oauth/{provider}/login, /oauth/{provider}/callback
                                "/login/oauth2/**",  // OAuth2 callback: /login/oauth2/code/{provider}
                                "/oauth2/authorization/**",
                                "/login/**"
                        ).permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                
                // Exception handling for authentication failures
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(oAuth2AuthenticationEntryPoint)
                )
                
                // OAuth2 login configuration
                // NOTE: We use custom /oauth/{provider}/login endpoints that redirect directly to provider auth pages
                // Spring Security's default /oauth2/authorization/{provider} is NOT used
                .oauth2Login(oauth2 -> oauth2
                        // Set login page to our custom endpoint to prevent default OAuth2 selection page
                        .loginPage("/oauth/kakao/login")
                        // Disable default authorization endpoint - redirect to our custom endpoints
                        .authorizationEndpoint(authorization -> authorization
                                // Use Spring's default base URI so registrationIds are properly resolved
                                .baseUri("/oauth2/authorization")
                        )
                        // Use default callback (/login/oauth2/code/{registrationId})
                        // User info endpoint configuration
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                        // Success handler
                        .successHandler(oAuth2SuccessHandler)
                );
        
        // Disable Spring Security default login page generator (/login)
        http.apply(new DefaultLoginPageConfigurer<>());
        http.getConfigurer(DefaultLoginPageConfigurer.class).disable();

        // Frame options for iframe usage
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    /**
     * Allow frontend origin for API calls with Authorization header.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8081"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
