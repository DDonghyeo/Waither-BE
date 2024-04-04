package com.waither.userservice.accounts.service;

import com.waither.userservice.accounts.dto.RegisterRequestDto;
import com.waither.userservice.accounts.entity.User;
import com.waither.userservice.accounts.repository.UserRepository;
import com.waither.userservice.common.response.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void signup(RegisterRequestDto requestDto) {

        // Todo : 이메일 인증
        // 비밀번호 인코딩
        String encodedPw = passwordEncoder.encode(requestDto.password());
        User newUser = requestDto.toEntity(encodedPw);
        userRepository.save(newUser);
    }
}
