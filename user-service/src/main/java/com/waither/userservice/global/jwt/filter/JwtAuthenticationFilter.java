package com.waither.userservice.global.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waither.userservice.dto.request.UserReqDto;
import com.waither.userservice.global.jwt.dto.JwtDto;
import com.waither.userservice.global.jwt.execption.SecurityErrorCode;
import com.waither.userservice.global.jwt.util.HttpResponseUtil;
import com.waither.userservice.global.response.ApiResponse;
import com.waither.userservice.global.jwt.userdetails.PrincipalDetails;
import com.waither.userservice.global.jwt.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // /login 요청을 하면, 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.info("JwtAuthenticationFilter : 로그인 시도 중");

        // request에 있는 username과 password를 파싱해서 자바 Object로 받기
        ObjectMapper om = new ObjectMapper();
        UserReqDto.LoginRequestDto loginRequestDto;
        try {
            loginRequestDto = om.readValue(request.getInputStream(), UserReqDto.LoginRequestDto.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Error of request body.");
        }

        // 유저네임패스워드 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.email(),
                        loginRequestDto.password());

        // PrincipalDetailsService의 loadUserByUsername() 함수가 실행되고 정상이면 authentication이 return됨.
        // Token 넣어서 던져서 인증 끝나면 authentication을 주고, 로그인 한 정보가 담긴다.
        // DB에 있는 username과 password가 일치한다는 뜻
        return authenticationManager.authenticate(authenticationToken);
        // authenticate() 메서드가 호출된 직후에 해당되는데,
        // 실제 비밀번호는 AuthenticationManager의 구현체인 ProviderManager에서 password는 제거됨.
    }

    // JWT Token 생성해서 response에 담아주기
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException {

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        log.info("[*] Login Success! - Login with " + principalDetails.getUsername());
        JwtDto jwtDto = new JwtDto(
                jwtUtil.createJwtAccessToken(principalDetails),
                jwtUtil.createJwtRefreshToken(principalDetails)
        );

        log.info("Access Token: " + jwtDto.accessToken());
        log.info("Refresh Token: " + jwtDto.refreshToken());

        HttpResponseUtil.setSuccessResponse(response, jwtDto);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException {

        // 실패한 인증에 따라 적절한 오류 메시지 설정
        log.info("[*] Login Fail");

        SecurityErrorCode errorCode;
        if (failed instanceof UsernameNotFoundException) {
            errorCode = SecurityErrorCode.USER_NOT_FOUND;
        } else if (failed instanceof BadCredentialsException) {
            errorCode = SecurityErrorCode.BAD_CREDENTIALS;
        } else if (failed instanceof LockedException || failed instanceof DisabledException) {
            errorCode = SecurityErrorCode.FORBIDDEN;
        } else if (failed instanceof AuthenticationServiceException) {
            errorCode = SecurityErrorCode.INTERNAL_SECURITY_ERROR;
        } else {
            errorCode = SecurityErrorCode.UNAUTHORIZED;
        }

        HttpResponseUtil.setErrorResponse(
                response,
                errorCode.getHttpStatus(),
                ApiResponse.onFailure(
                        errorCode.getCode(),
                        errorCode.getMessage(),
                        null
                )
        );

    }

}
