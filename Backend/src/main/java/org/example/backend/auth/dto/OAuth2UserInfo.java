package org.example.backend.auth.dto;

import java.util.Map;

/**
 * Abstract class for converting provider-specific OAuth2 response structures to common format
 * Each social login provider (Kakao, Naver, Google, etc.) has different API response structures
 * This class provides a unified interface
 */
public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * User unique ID issued by social login provider
     * @return User unique ID (as String)
     */
    public abstract String getId();

    /**
     * User email
     * @return Email address (null if consent was not given)
     */
    public abstract String getEmail();

    /**
     * User name (nickname)
     * @return User name or nickname
     */
    public abstract String getName();
}

