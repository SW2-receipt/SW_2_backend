package org.example.backend.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * OAuth2 Login Controller
 * Handles OAuth2 login initiation and callback routing
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuth2Controller {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.scope}")
    private String kakaoScope;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${spring.security.oauth2.client.registration.naver.scope}")
    private String naverScope;

    @Value("${server.port:8080}")
    private int serverPort;

    /**
     * Kakao login endpoint
     * Redirects directly to Kakao OAuth2 authorization page
     * GET /oauth/kakao/login
     */
    @GetMapping("/oauth/kakao/login")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        log.info("Kakao login initiated - forwarding to Spring OAuth2 authorization endpoint");
        // Use Spring Security-managed authorization flow (handles state/session correctly)
        response.sendRedirect("/oauth2/authorization/kakao");
    }

    /**
     * Kakao callback endpoint
     * Receives callback from Kakao and forwards to Spring Security's default callback
     * GET /oauth/kakao/callback
     */
    @GetMapping("/oauth/kakao/callback")
    public void kakaoCallback(@RequestParam(required = false) String code,
                              @RequestParam(required = false) String state,
                              @RequestParam(required = false) String error,
                              HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        log.info("Kakao callback received - code: {}, state: {}, error: {}", code, state, error);
        
        // Forward to Spring Security's default callback endpoint
        String queryString = request.getQueryString();
        String redirectUrl = "/login/oauth2/code/kakao" + (queryString != null ? "?" + queryString : "");
        log.info("Redirecting to Spring Security callback: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    /**
     * Naver login endpoint
     * Redirects directly to Naver OAuth2 authorization page
     * GET /oauth/naver/login
     */
    @GetMapping("/oauth/naver/login")
    public void naverLogin(HttpServletResponse response) throws IOException {
        log.info("Naver login initiated - forwarding to Spring OAuth2 authorization endpoint");
        // Use Spring Security-managed authorization flow (handles state/session correctly)
        response.sendRedirect("/oauth2/authorization/naver");
    }

    /**
     * Naver callback endpoint
     * Receives callback from Naver and forwards to Spring Security's default callback
     * GET /oauth/naver/callback
     */
    @GetMapping("/oauth/naver/callback")
    public void naverCallback(@RequestParam(required = false) String code,
                              @RequestParam(required = false) String state,
                              @RequestParam(required = false) String error,
                              HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        log.info("Naver callback received - code: {}, state: {}, error: {}", code, state, error);
        
        // Forward to Spring Security's default callback endpoint
        String queryString = request.getQueryString();
        String redirectUrl = "/login/oauth2/code/naver" + (queryString != null ? "?" + queryString : "");
        log.info("Redirecting to Spring Security callback: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    /**
     * Handle /login requests - redirect to prevent Spring Security default OAuth2 selection page
     * GET /login
     */
    /**
     * Any access to Spring Security's default OAuth2 authorization endpoints should be redirected
     * to our custom endpoints so the default selection page never appears.
     */
    @GetMapping("/oauth2/authorization/{provider}")
    public void redirectDefaultAuthorization(@PathVariable String provider,
                                             HttpServletResponse response) throws IOException {
        log.warn("Received default OAuth2 authorization request for provider: {}. Redirecting to custom login.", provider);
//        switch (provider.toLowerCase()) {
//            case "kakao" -> response.sendRedirect("/oauth/kakao/login");
//            case "naver" -> response.sendRedirect("/oauth/naver/login");
//            default -> response.sendRedirect("http://localhost:8081/");
//        }
    }

    /**
     * Catch-all for /login or /oauth2 paths without provider to prevent default login page exposure.
     */
    @GetMapping({"/login/oauth2/authorization", "/oauth2/authorization"})
    public void redirectWithoutProvider(HttpServletResponse response) throws IOException {
        log.warn("Default OAuth2 authorization page requested without provider. Redirecting to frontend login.");
        response.sendRedirect("http://localhost:8081/");
    }

}
