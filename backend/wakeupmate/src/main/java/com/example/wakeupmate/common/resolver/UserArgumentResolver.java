package com.example.wakeupmate.common.resolver;

import com.example.wakeupmate.user.domain.User;
import com.example.wakeupmate.user.dto.CustomOauth2User;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        Authentication authentication = (Authentication) webRequest.getUserPrincipal();

        if (authentication != null && authentication.getPrincipal() instanceof CustomOauth2User customUser) {
            return customUser.getUser();
        }

        return null;
    }
}