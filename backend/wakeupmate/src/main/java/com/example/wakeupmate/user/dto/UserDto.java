package com.example.wakeupmate.user.dto;

import com.example.wakeupmate.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDto {
    private final String socialLoginId;
    private final String username;
    private final String email;
    private final String profileImageUrl;
    private final String role;

    @Builder
    public UserDto(String socialLoginId, String username, String email, String profileImageUrl, String role) {
        this.socialLoginId = socialLoginId;
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }

    public static UserDto of(User user) {
        return UserDto.builder()
                .socialLoginId(user.getSocialLoginId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .build();
    }
}
