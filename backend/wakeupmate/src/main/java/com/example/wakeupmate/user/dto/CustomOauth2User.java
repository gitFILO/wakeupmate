package com.example.wakeupmate.user.dto;

import com.example.wakeupmate.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Getter
public class CustomOauth2User implements OAuth2User {

    private final User user;

    public CustomOauth2User(User user) {
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", user.getSocialLoginId());
        attributes.put("username", user.getUsername());
        attributes.put("email", user.getEmail());
        attributes.put("profileImageUrl", user.getProfileImageUrl());
        attributes.put("role", user.getRole());
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> user.getRole());
    }

    @Override
    public String getName() {
        return user.getUsername();
    }

    public Long getUserId(){
        return user.getId();
    }
}
