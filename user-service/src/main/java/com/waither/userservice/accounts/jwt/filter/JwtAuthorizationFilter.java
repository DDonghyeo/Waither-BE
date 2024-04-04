package com.waither.userservice.accounts.jwt.filter;

import com.waither.userservice.accounts.jwt.userdetails.PrincipalDetails;
import com.waither.userservice.accounts.jwt.util.JwtUtil;
import com.waither.userservice.accounts.jwt.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("[*] Jwt Filter");
        if (request.getServletPath().equals("/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        String email = request.getHeader("email");
        log.info("email = {}", email);

        filterChain.doFilter(request, response);

    }
}