package com.example.wakeupmate.user.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {
    private final String id;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;

    @SuppressWarnings("unchecked")
    public KakaoResponse(Map<String, Object> attributes) {
        this.id = String.valueOf(attributes.get("id"));
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        this.nickname = properties != null ? (String) properties.get("nickname") : null;
        this.profileImageUrl = properties != null ? (String) properties.get("profile_image") : null;
        this.email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return nickname;
    }

    @Override
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
