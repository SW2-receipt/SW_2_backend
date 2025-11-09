package org.example.backend.User.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.User.Domain.User;
import org.example.backend.User.Repository.UserRepository;
import org.example.backend.User.Dto.UserResponseDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * User API Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    /**
     * Get current logged-in user information
     * @param oAuth2User Current logged-in OAuth2 user
     * @return User information
     */
    @GetMapping("/me")
    public UserResponseDto getCurrentUser(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            throw new IllegalStateException("User is not logged in");
        }

        // Extract provider and oauthId from OAuth2User
        String oauthId = String.valueOf(oAuth2User.getAttribute("id"));
        String provider = extractProvider(oAuth2User);

        // Retrieve user information from database
        User user = userRepository.findByProviderAndOauthId(provider, oauthId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * Extract provider from OAuth2 user information
     * OAuth2UserService adds provider information to attributes
     * @param oAuth2User OAuth2 user
     * @return Provider name (kakao, naver, google, etc.)
     */
    private String extractProvider(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        // OAuth2UserService adds provider information to attributes
        String provider = (String) attributes.get("provider");
        if (provider != null) {
            return provider;
        }
        // Fallback: For Kakao, can distinguish by specific field
        if (attributes.containsKey("kakao_account")) {
            return "kakao";
        }
        // Should not reach here normally
        throw new IllegalStateException("Provider information not found");
    }

    /**
     * Debug: Get OAuth2 original attributes
     * @param oAuth2User OAuth2 user
     * @return OAuth2 original attributes
     */
    @GetMapping("/me/attributes")
    public Map<String, Object> getOAuth2Attributes(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            throw new IllegalStateException("User is not logged in");
        }
        return oAuth2User.getAttributes();
    }
}
