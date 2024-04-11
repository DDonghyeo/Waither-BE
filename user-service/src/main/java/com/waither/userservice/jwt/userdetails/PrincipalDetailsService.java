package com.waither.userservice.jwt.userdetails;

import com.waither.userservice.entity.User;
import com.waither.userservice.jwt.execption.SecurityCustomException;
import com.waither.userservice.jwt.execption.SecurityErrorCode;
import com.waither.userservice.repository.UserRepository;
import com.waither.userservice.global.exception.CustomException;
import com.waither.userservice.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> userEntity = userRepository.findByEmail(email);
        if (userEntity.isPresent()) {
            User user = userEntity.get();
            return new PrincipalDetails(user.getEmail(),user.getPassword(), user.getRole());
        }
        throw new UsernameNotFoundException("사용자가 존재하지 않습니다.");
    }
}