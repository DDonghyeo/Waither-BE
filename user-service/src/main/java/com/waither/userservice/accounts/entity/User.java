package com.waither.userservice.accounts.entity;

import com.waither.userservice.accounts.entity.type.UserStatus;
import com.waither.userservice.common.BaseEntity;
import com.waither.userservice.survey.entity.Survey;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user")
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 이메일
    @Column(name = "email", nullable = false)
    private String email;

    // 유저 비밀번호
    @Column(name = "password", nullable = false)
    private String password;

    // 유저 닉네임
    @Column(name = "nickname", nullable = false)
    private String nickname;

    // 유저 상태 (active / 휴면 / 탈퇴 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    // 권한
    @Column(name = "role", nullable = false)
    private String role; //ROLE_USER or ROLE_ADMIN

    // Todo: OAuth 에서 이미지 가져오기
    // 프로필 이미지
    private String image;

    // 사용자 맞춤 서비스 허용 여부
    @Column(name = "custom", nullable = false)
    private boolean custom;

}
