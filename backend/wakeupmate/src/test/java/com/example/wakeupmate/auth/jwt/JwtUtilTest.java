package com.example.wakeupmate.auth.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String testSecretKey = "testSecretKeyForJwtTokenGenerationAndValidation1234567890";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(testSecretKey);
    }

    @Test
    @DisplayName("JWT 토큰을 생성할 수 있다.")
    void createJwt() {
        // given
        Long userId = 1L;
        String role = "ROLE_USER";
        Long expiredMs = 60 * 60 * 1000L; // 1시간

        // when
        String token = jwtUtil.createJwt(userId, role, expiredMs);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT는 header.payload.signature 형태
    }

    @Test
    @DisplayName("JWT 토큰에서 사용자 ID를 추출할 수 있다.")
    void getUserId() {
        // given
        Long userId = 1L;
        String role = "ROLE_USER";
        Long expiredMs = 60 * 60 * 1000L;
        String token = jwtUtil.createJwt(userId, role, expiredMs);

        // when
        Long extractedUserId = jwtUtil.getUserId(token);

        // then
        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("JWT 토큰에서 역할을 추출할 수 있다.")
    void getRole() {
        // given
        Long userId = 1L;
        String role = "ROLE_USER";
        Long expiredMs = 60 * 60 * 1000L;
        String token = jwtUtil.createJwt(userId, role, expiredMs);

        // when
        String extractedRole = jwtUtil.getRole(token);

        // then
        assertThat(extractedRole).isEqualTo(role);
    }

    @Test
    @DisplayName("JWT 토큰의 만료 여부를 확인할 수 있다.")
    void isExpired() {
        // given
        Long userId = 1L;
        String role = "ROLE_USER";
        Long expiredMs = 60 * 60 * 1000L; // 1시간 후 만료
        String token = jwtUtil.createJwt(userId, role, expiredMs);

        // when
        Boolean isExpired = jwtUtil.isExpired(token);

        // then
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("만료된 JWT 토큰을 감지할 수 있다.")
    void isExpired_ExpiredToken() {
        // given
        Long userId = 1L;
        String role = "ROLE_USER";
        Long expiredMs = 1L; // 즉시 만료
        String token = jwtUtil.createJwt(userId, role, expiredMs);

        // 토큰이 만료동안 대기
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // when & then
        assertThatThrownBy(() -> jwtUtil.isExpired(token))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰에 대해 예외가 발생한다.")
    void invalidToken() {
        // given
        String invalidToken = "invalid.jwt.token";

        // when & then
        assertThatThrownBy(() -> jwtUtil.getUserId(invalidToken))
                .isInstanceOf(Exception.class);
    }
} 