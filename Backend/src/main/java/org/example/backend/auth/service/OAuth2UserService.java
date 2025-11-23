package org.example.backend.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.auth.dto.KakaoUserInfo;
import org.example.backend.auth.dto.NaverUserInfo;
import org.example.backend.auth.dto.OAuth2UserInfo;
import org.example.backend.User.Domain.Role;
import org.example.backend.User.Domain.User;
import org.example.backend.User.Repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 User Service
 * Processes user information from social login providers
 * Saves/retrieves user information from database
 * Converts to format usable by Spring Security
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * Load OAuth2 user information and save/retrieve from database
     * @param userRequest OAuth2 user request
     * @return OAuth2User for Spring Security authentication
     * @throws OAuth2AuthenticationException authentication exception
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Get user information from social login provider
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Identify provider (kakao, naver, google, etc.)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        log.info("OAuth2 login attempt - Provider: {}", registrationId);
        log.debug("OAuth2 Attributes: {}", attributes);

        // Convert provider-specific response structure to common interface
        OAuth2UserInfo userInfo = getOAuth2UserInfo(registrationId, attributes);

        // Retrieve or register user in database
        User user = saveOrUpdateUser(userInfo, registrationId);

        // Add provider information to attributes (for OAuth2SuccessHandler)
        Map<String, Object> modifiedAttributes = new HashMap<>(attributes);
        modifiedAttributes.put("provider", registrationId);
        modifiedAttributes.put("userId", user.getId());
        modifiedAttributes.put("email", userInfo.getEmail());

        // Return in format usable by Spring Security
        // nameAttributeKey: key used to get user identifier from OAuth2User
        // For Kakao, use "id" field (top level)
        // For Naver, use "response" field (which contains the user info map)
        // Spring Security will use the provider's user-name-attribute setting
        String nameAttributeKey = "kakao".equals(registrationId) ? "id" : "response";
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                modifiedAttributes,
                nameAttributeKey
        );
    }

    /**
     * Create OAuth2UserInfo object by provider
     * @param registrationId Provider identifier (kakao, naver, google, etc.)
     * @param attributes OAuth2 user attributes
     * @return OAuth2UserInfo object
     */
    private OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return new KakaoUserInfo(attributes);
        } else if ("naver".equals(registrationId)) {
            return new NaverUserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId);
        }
    }

    /**
     * Save or update user information in database
     * @param userInfo OAuth2 user information
     * @param registrationId Provider identifier
     * @return Saved User entity
     */
    private User saveOrUpdateUser(OAuth2UserInfo userInfo, String registrationId) {
        // Find existing user by provider and oauthId
        return userRepository
                .findByProviderAndOauthId(registrationId, userInfo.getId())
                .map(existingUser -> {
                    // Update existing user information (email, name may have changed)
                    log.info("Existing user login - ID: {}, Email: {}", existingUser.getId(), existingUser.getEmail());
                    existingUser.setEmail(userInfo.getEmail());
                    existingUser.setName(userInfo.getName());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // Save new user
                    User newUser = User.builder()
                            .provider(registrationId)
                            .oauthId(userInfo.getId())
                            .email(userInfo.getEmail())
                            .name(userInfo.getName())
                            .role(Role.USER)
                            .build();
                    log.info("New user registration - Provider: {}, OAuthId: {}, Email: {}", 
                            registrationId, userInfo.getId(), userInfo.getEmail());
                    return userRepository.save(newUser);
                });
    }
}

