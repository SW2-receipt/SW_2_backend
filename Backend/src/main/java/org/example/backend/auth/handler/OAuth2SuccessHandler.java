package org.example.backend.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.auth.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2 Login Success Handler
 * Handles actions after successful OAuth2 login
 * Generates JWT token and redirects to frontend with token in query parameter
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenService jwtTokenService;

    /**
     * Frontend base URL
     * Uses FRONTEND_BASE environment variable or app.frontend.base from application.properties
     * Default: http://localhost:8081
     */
    @Value("${FRONTEND_BASE:${app.frontend.base:http://localhost:8081}}")
    private String frontendBase;

    /**
     * Method called when OAuth2 login succeeds
     * @param request HTTP request
     * @param response HTTP response
     * @param authentication Authentication information (includes OAuth2User)
     * @throws IOException IO exception
     * @throws ServletException Servlet exception
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        // Extract user information from OAuth2User
        Long userId = (Long) oAuth2User.getAttribute("userId");
        String email = (String) oAuth2User.getAttribute("email");
        String provider = (String) oAuth2User.getAttribute("provider");
        if (provider == null || provider.isBlank()) {
            // Fallback to kakao so frontend path (/auth/{provider}/callback) stays valid
            log.warn("Provider attribute missing in OAuth2User. Falling back to 'kakao'. Attributes: {}", oAuth2User.getAttributes().keySet());
            provider = "kakao";
        }
        
        log.info("OAuth2 login success - User ID: {}, Email: {}, Provider: {}", userId, email, provider);
        
        // Generate JWT token
        String jwtToken = jwtTokenService.generateToken(userId, email, provider);
        log.debug("JWT token generated for user ID: {}", userId);
        
        // Redirect to frontend OAuth callback (frontend will set isLoggedIn and push /main)
        // Format: ${FRONTEND_BASE}/auth/{provider}/callback?token=<JWT>
        String redirectUrl = frontendBase + "/auth/" + provider + "/callback?token=" + jwtToken;
        log.info("Redirecting to frontend: {}", redirectUrl);
        
        response.sendRedirect(redirectUrl);
    }
}

