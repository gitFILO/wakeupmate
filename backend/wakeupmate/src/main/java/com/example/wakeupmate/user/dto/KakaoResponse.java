package com.example.wakeupmate.user.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;
    private final Object id;

    public KakaoResponse(Map<String, Object> attribute) {
        this.id = attribute.get("id");
        this.kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        this.profile = (Map<String, Object>) kakaoAccount.get("profile");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return id.toString();
    }

    @Override
    public String getEmail() {
        return kakaoAccount.get("email") != null ? kakaoAccount.get("email").toString() : null;
    }

    @Override
    public String getName() {
        return profile.get("nickname") != null ? profile.get("nickname").toString() : null;
    }
}
