package org.example.backend.auth.dto;

import java.util.Map;

/**
 * Naver OAuth2 User Info Handler
 * Naver API Response Structure:
 * {
 *   "resultcode": "00",
 *   "message": "success",
 *   "response": {
 *     "id": "네이버 고유 ID",
 *     "email": "user@example.com",
 *     "name": "UserNickname"
 *   }
 * }
 */
@SuppressWarnings("unchecked")
public class NaverUserInfo extends OAuth2UserInfo {

    public NaverUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        // Naver provides id inside "response" object
        Object responseObj = attributes.get("response");
        if (responseObj == null || !(responseObj instanceof Map)) {
            return null;
        }
        Map<String, Object> response = (Map<String, Object>) responseObj;
        Object id = response.get("id");
        return id != null ? String.valueOf(id) : null;
    }

    @Override
    public String getEmail() {
        // Naver has email inside "response" object
        Object responseObj = attributes.get("response");
        if (responseObj == null || !(responseObj instanceof Map)) {
            return null;
        }
        Map<String, Object> response = (Map<String, Object>) responseObj;
        Object emailObj = response.get("email");
        return emailObj != null ? String.valueOf(emailObj) : null;
    }

    @Override
    public String getName() {
        // Naver has name inside "response" object
        Object responseObj = attributes.get("response");
        if (responseObj == null || !(responseObj instanceof Map)) {
            return getDefaultName();
        }
        
        Map<String, Object> response = (Map<String, Object>) responseObj;
        Object nameObj = response.get("name");
        String name = nameObj != null ? String.valueOf(nameObj) : null;
        
        // Use part before @ in email or default value if name is not available
        if (name == null || name.isEmpty()) {
            return getDefaultName();
        }
        return name;
    }

    /**
     * Return default name (extract from email or use default value)
     */
    private String getDefaultName() {
        String email = getEmail();
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        }
        return "NaverUser";
    }
}


