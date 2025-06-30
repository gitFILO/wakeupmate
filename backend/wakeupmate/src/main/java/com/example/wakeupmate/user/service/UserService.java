package com.example.wakeupmate.user.service;

import com.example.wakeupmate.user.domain.User;
import com.example.wakeupmate.user.dto.CustomOauth2User;
import com.example.wakeupmate.user.dto.KakaoResponse;
import com.example.wakeupmate.user.dto.OAuth2Response;
import com.example.wakeupmate.user.dto.UserDto;
import com.example.wakeupmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@RequiredArgsConstructor
@Service
public class UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response;

        if ("kakao".equals(registrationId)) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId);
        }

        String userSocialLoginId = createSocialLoginId(oAuth2Response);

        User user = userRepository.findBySocialLoginId(userSocialLoginId)
                .orElseGet(() -> addUser(oAuth2Response));

        return new CustomOauth2User(user);
    }

    private Long loginUser(User existingUser) {

        return existingUser.getId();
    }

    private User addUser(OAuth2Response oAuth2Response) {
        User user = User.builder()
                .socialLoginId(createSocialLoginId(oAuth2Response))
                .profileImageUrl(oAuth2Response.getProfileImageUrl())
                .username(oAuth2Response.getName())
                .email(oAuth2Response.getEmail())
                .role("ROLE_USER")
                .build();

        userRepository.save(user);

        return user;
}

private String createSocialLoginId(OAuth2Response oAuth2Response) {
        return oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
    }

    private UserDto getUserDtoFromOauth2Response(OAuth2Response oAuth2Response) {

        return UserDto.builder()
                .socialLoginId(createSocialLoginId(oAuth2Response))
                .username(oAuth2Response.getName())
                .email(oAuth2Response.getEmail())
                .profileImageUrl(oAuth2Response.getProfileImageUrl())
                .role("ROLE_USER")
                .build();
    }
}
