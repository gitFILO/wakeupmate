package com.example.wakeupmate.auth.jwt;

import com.example.wakeupmate.common.exception.ExceptionCode;
import com.example.wakeupmate.common.exception.InvalidJwtException;
import com.example.wakeupmate.user.domain.User;
import com.example.wakeupmate.user.dto.CustomOauth2User;
import com.example.wakeupmate.user.dto.UserDto;
import com.example.wakeupmate.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = getAuthorizationToken(request);

        if (isTokenInvalid(authorization)) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtUtil.getUserId(authorization);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidJwtException(ExceptionCode.FAILED_TO_VALIDATE_TOKEN));

        UserDto userDTO = UserDto.of(user);

        CustomOauth2User customOAuth2User = new CustomOauth2User(userId,userDTO);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private boolean isTokenInvalid(String token) {
        return token == null || jwtUtil.isExpired(token);
    }

    private String getAuthorizationToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> "Authorization".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

}
