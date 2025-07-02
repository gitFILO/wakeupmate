package com.example.wakeupmate.auth.handler;

import com.example.wakeupmate.auth.jwt.JwtUtil;
import com.example.wakeupmate.user.dto.CustomOauth2User;
import com.example.wakeupmate.user.domain.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomSuccessHandlerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    private CustomSuccessHandler customSuccessHandler;

    @BeforeEach
    void setUp() {
        customSuccessHandler = new CustomSuccessHandler(jwtUtil);
    }

    @Test
    @DisplayName("인증 성공 시 JWT 토큰을 생성하고 쿠키에 설정한다.")
    void onAuthenticationSuccess_CreateJwtAndSetCookie() throws Exception {
        // given
        User user = createTestUser();
        CustomOauth2User customOauth2User = new CustomOauth2User(user);
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        String expectedToken = "test-jwt-token";

        when(authentication.getPrincipal()).thenReturn(customOauth2User);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(jwtUtil.createJwt(anyLong(), anyString(), anyLong())).thenReturn(expectedToken);

        // when
        customSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(jwtUtil).createJwt(user.getId(), "ROLE_USER", 60 * 60 * 60L);
        
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        
        Cookie capturedCookie = cookieCaptor.getValue();
        assertThat(capturedCookie.getName()).isEqualTo("Authorization");
        assertThat(capturedCookie.getValue()).isEqualTo(expectedToken);
        assertThat(capturedCookie.getMaxAge()).isEqualTo(60 * 60 * 60);
        assertThat(capturedCookie.isHttpOnly()).isTrue();
        assertThat(capturedCookie.getSecure()).isTrue();
        assertThat(capturedCookie.getPath()).isEqualTo("/");
    }

    @Test
    @DisplayName("인증 성공 시 환경 변수가 설정되어 있으면 해당 URL로 리다이렉트한다.")
    void onAuthenticationSuccess_RedirectWithEnvironmentVariable() throws Exception {
        // given
        User user = createTestUser();
        CustomOauth2User customOauth2User = new CustomOauth2User(user);
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        String expectedToken = "test-jwt-token";

        when(authentication.getPrincipal()).thenReturn(customOauth2User);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(jwtUtil.createJwt(anyLong(), anyString(), anyLong())).thenReturn(expectedToken);

        // when
        customSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(response).sendRedirect(anyString());
    }

    @Test
    @DisplayName("인증 성공 시 기본 URL로 리다이렉트한다.")
    void onAuthenticationSuccess_RedirectToDefaultUrl() throws Exception {
        // given
        User user = createTestUser();
        CustomOauth2User customOauth2User = new CustomOauth2User(user);
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        String expectedToken = "test-jwt-token";

        when(authentication.getPrincipal()).thenReturn(customOauth2User);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(jwtUtil.createJwt(anyLong(), anyString(), anyLong())).thenReturn(expectedToken);

        // when
        customSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(response).sendRedirect(anyString());
    }

    private User createTestUser() {
        User user = User.builder()
                .socialLoginId("kakao_123456789")
                .username("테스트사용자")
                .email("test@example.com")
                .profileImageUrl("https://example.com/profile.jpg")
                .role("ROLE_USER")
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }
} 