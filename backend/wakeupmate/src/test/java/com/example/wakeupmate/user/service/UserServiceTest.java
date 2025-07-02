package com.example.wakeupmate.user.service;

import com.example.wakeupmate.user.domain.User;
import com.example.wakeupmate.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("기존 사용자를 소셜 로그인 ID로 조회할 수 있다.")
    void findBySocialLoginId_ExistingUser() {
        // given
        User existingUser = createTestUser();
        String socialLoginId = "kakao_123456789";
        
        when(userRepository.findBySocialLoginId(socialLoginId)).thenReturn(Optional.of(existingUser));

        // when
        Optional<User> result = userRepository.findBySocialLoginId(socialLoginId);
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getSocialLoginId()).isEqualTo(socialLoginId);
        verify(userRepository).findBySocialLoginId(socialLoginId);
    }

    @Test
    @DisplayName("새로운 사용자를 저장할 수 있다.")
    void saveUser() {
        // given
        User newUser = createTestUser();
        
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // when
        User savedUser = userRepository.save(newUser);
        
        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getSocialLoginId()).isEqualTo("kakao_123456789");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 빈 Optional을 반환한다.")
    void findBySocialLoginId_NonExistingUser() {
        // given
        String socialLoginId = "kakao_nonexisting";
        
        when(userRepository.findBySocialLoginId(socialLoginId)).thenReturn(Optional.empty());

        // when
        Optional<User> result = userRepository.findBySocialLoginId(socialLoginId);
        
        // then
        assertThat(result).isEmpty();
        verify(userRepository).findBySocialLoginId(socialLoginId);
    }

    private User createTestUser() {
        return User.builder()
                .socialLoginId("kakao_123456789")
                .username("테스트사용자")
                .email("test@example.com")
                .profileImageUrl("https://example.com/profile.jpg")
                .role("ROLE_USER")
                .build();
    }
} 