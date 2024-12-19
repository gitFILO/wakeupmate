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
public class UserService extends DefaultOAuth2UserService{
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response;

        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else {
            oAuth2Response = null;
            return null;
        }

        String userName = createUsername(oAuth2Response);

        userRepository.findByUsername(userName)
                .ifPresentOrElse(
                        existingUser -> updateUser(existingUser, oAuth2Response),
                        () -> addUser(oAuth2Response)
                );

        return createOAuth2UserFromOauth2Response(oAuth2Response);
    }

    private void updateUser(User existingUser, OAuth2Response oAuth2Response) {

        existingUser.update(
                createUsername(oAuth2Response),
                oAuth2Response.getEmail()
        );

        userRepository.save(existingUser);
    }

    private void addUser(OAuth2Response oAuth2Response) {

        User user = User.builder()
                .username(createUsername(oAuth2Response))
                .email(oAuth2Response.getEmail())
                .build();

        userRepository.save(user);
    }

    private String createUsername(OAuth2Response oAuth2Response) {

        return oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
    }

    private UserDto getUserDtoFromOauth2Response(OAuth2Response oAuth2Response) {

        return UserDto.builder()
                .username(createUsername(oAuth2Response))
                .name(oAuth2Response.getName())
                .role("ROLE_USER")
                .build();
    }

    private OAuth2User createOAuth2UserFromOauth2Response(OAuth2Response oAuth2Response) {
        UserDto userDto = getUserDtoFromOauth2Response(oAuth2Response);

        return new CustomOauth2User(userDto);
    }
}
