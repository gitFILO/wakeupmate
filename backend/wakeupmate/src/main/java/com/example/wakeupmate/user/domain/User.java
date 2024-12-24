package com.example.wakeupmate.user.domain;

import com.example.wakeupmate.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "USERS")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String socialLoginId;

    private String profileImageUrl;

    private String username;

    private String password;

    private String email;

    private String role;

    @Builder
    public User(String socialLoginId, String profileImageUrl, String username, String email, String role) {
        this.socialLoginId = socialLoginId;
        this.profileImageUrl = profileImageUrl;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public void update(String username, String email) {
        this.email = email;
        this.username = username;
    }
}
