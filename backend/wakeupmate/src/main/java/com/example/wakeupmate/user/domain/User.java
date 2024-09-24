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


    @Builder
    public User(String socialLoginId, String nickname, String profileImageUrl) {
        this.socialLoginId = socialLoginId;
        this.username = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
