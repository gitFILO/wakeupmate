package com.example.wakeupmate.user.controller;

import com.example.wakeupmate.user.domain.User;
import com.example.wakeupmate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLOutput;
import java.util.Map;

@RequestMapping("/v1/users")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String username = (String) attributes.get("username");
        String profileImageUrl = (String) attributes.get("profileImageUrl");

        return ResponseEntity.ok(Map.of(
                "username", username,
                "profileImageUrl", profileImageUrl
        ));
    }
}
