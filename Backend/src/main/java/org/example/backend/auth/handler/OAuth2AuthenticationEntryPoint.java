package org.example.backend.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Authentication Entry Point
 * Handles unauthorized access attempts
 * Redirects to OAuth2 login page when user is not authenticated
 */
@Slf4j
@Component
public class OAuth2AuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        log.warn("Unauthorized access attempt - URI: {}, Message: {}", 
                request.getRequestURI(), authException.getMessage());
        
        // For API requests, return 401 Unauthorized
        if (request.getRequestURI().startsWith("/api/")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required. Please login first.\"}");
            response.getWriter().flush();
        } else {
            // For web requests, redirect to OAuth2 login
            response.sendRedirect("/oauth/kakao/login");
        }
    }
}
