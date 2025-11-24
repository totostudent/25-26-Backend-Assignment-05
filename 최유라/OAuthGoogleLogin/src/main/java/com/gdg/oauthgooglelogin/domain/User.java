package com.gdg.oauthgooglelogin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User { //DB에서 쓸 user

    @Id
    @Column(name = "user_Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", nullable = false)
    private String email;

    @Column(name = "user_password")
    private String password; //자체 회원가입 시에만 사용, 소셜 로그인 시에는 비어 있음

    @Column(name = "user_name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_provider", nullable = false)
    private Provider provider; //GOOGLE

    @Column(name = "user_profile")
    private String profileUrl; //소셜 로그인 이후 전달받는 '구글 계정 프로필 사진 url'

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private Role role;

    @Builder
    public User(Long id, String email, String password, String name, Provider provider, String profileUrl, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.name = name;
        this.profileUrl = profileUrl;
        this.role = role;
    }
}
