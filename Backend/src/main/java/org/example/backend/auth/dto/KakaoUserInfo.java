package org.example.backend.auth.dto;

import java.util.Map;

/**
 * Kakao OAuth2 User Info Handler
 * Kakao API Response Structure:
 * {
 *   "id": 123456789,
 *   "kakao_account": {
 *     "email": "user@example.com",
 *     "profile": {
 *       "nickname": "UserNickname"
 *     }
 *   }
 * }
 */
@SuppressWarnings("unchecked")
public class KakaoUserInfo extends OAuth2UserInfo {

    public KakaoUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        // Kakao provides id as Long type at the top level
        Object id = attributes.get("id");
        if (id == null) {
            return null;
        }
        return String.valueOf(id);
    }

    @Override
    public String getEmail() {
        // Kakao has email inside kakao_account
        // May be null if email consent was not given
        Object kakaoAccountObj = attributes.get("kakao_account");
        if (kakaoAccountObj == null || !(kakaoAccountObj instanceof Map)) {
            return null;
        }
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;
        Object emailObj = kakaoAccount.get("email");
        return emailObj != null ? String.valueOf(emailObj) : null;
    }

    @Override
    public String getName() {
        // Kakao has nickname at kakao_account.profile.nickname
        Object kakaoAccountObj = attributes.get("kakao_account");
        if (kakaoAccountObj == null || !(kakaoAccountObj instanceof Map)) {
            return getDefaultName();
        }
        
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;
        Object profileObj = kakaoAccount.get("profile");
        if (profileObj == null || !(profileObj instanceof Map)) {
            return getDefaultName();
        }
        
        Map<String, Object> profile = (Map<String, Object>) profileObj;
        Object nicknameObj = profile.get("nickname");
        String nickname = nicknameObj != null ? String.valueOf(nicknameObj) : null;
        
        // Use part before @ in email or default value if nickname is not available
        if (nickname == null || nickname.isEmpty()) {
            return getDefaultName();
        }
        return nickname;
    }

    /**
     * Return default name (extract from email or use default value)
     */
    private String getDefaultName() {
        String email = getEmail();
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        }
        return "KakaoUser";
    }
}




