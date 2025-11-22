package org.example.backend.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2 Login Success Handler
 * Handles actions after successful OAuth2 login
 * Redirects to frontend after successful authentication
 */
@Slf4j
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    /**
     * Redirect URL after successful login
     * Uses app.oauth2.redirect-uri from application.properties
     * Example: http://localhost:3000 (frontend address)
     */
    @Value("${app.oauth2.redirect-uri:http://localhost:3000}")
    private String redirectUri;

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
        
        log.info("OAuth2 login success - User: {}", oAuth2User.getAttributes().get("id"));
        log.info("Redirecting to frontend: {}", redirectUri);
        
        // 프론트엔드로 리다이렉트 (성공 파라미터 포함)
        // 프론트엔드에서 이 파라미터를 확인하여 로그인 상태로 전환
        String redirectUrl = redirectUri + "?login=success";
        log.info("Final redirect URL: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}

