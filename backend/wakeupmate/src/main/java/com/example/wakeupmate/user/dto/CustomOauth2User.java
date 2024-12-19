package com.example.wakeupmate.user.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOauth2User implements OAuth2User {

    private final UserDto userDto;

    public CustomOauth2User(UserDto userDto) {

        this.userDto = userDto;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
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

        return userDto.getName();
    }

    public String getUsername(){

        return userDto.getUsername();
    }
}
