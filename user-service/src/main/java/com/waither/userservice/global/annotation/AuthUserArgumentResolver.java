package com.waither.userservice.global.annotation;

import com.waither.userservice.entity.User;
import com.waither.userservice.global.jwt.execption.SecurityCustomException;
import com.waither.userservice.global.jwt.execption.SecurityErrorCode;
import com.waither.userservice.global.jwt.util.JwtUtil;
import com.waither.userservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasParameterAnnotation = parameter.hasParameterAnnotation(AuthUser.class);
        boolean isUserParameterType = parameter.getParameterType().isAssignableFrom(User.class);
        return hasParameterAnnotation && isUserParameterType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String token = jwtUtil.resolveAccessToken(httpServletRequest);
        String email = jwtUtil.getEmail(token);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new SecurityCustomException(SecurityErrorCode.TOKEN_NOT_FOUND));
    }
}
