package com.example.wakeupmate.user.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.sql.SQLOutput;
import java.util.*;

public class CustomOauth2User implements OAuth2User {

    @Getter
    private final Long userId;
    private final UserDto userDto;

    public CustomOauth2User(Long userId,UserDto userDto) {

        this.userId = userId;
        this.userDto = userDto;
    }

    @Override
    public Map<String, Object> getAttributes() {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", userDto.getSocialLoginId());
        attributes.put("username", userDto.getUsername());
        attributes.put("email", userDto.getEmail());
        attributes.put("profileImageUrl", userDto.getProfileImageUrl());
        attributes.put("role", userDto.getRole());

        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return userDto.getRole();
            }
        });

        return authorities;
    }

    @Override
    public String getName() {

        return userDto.getUsername();
    }
}
