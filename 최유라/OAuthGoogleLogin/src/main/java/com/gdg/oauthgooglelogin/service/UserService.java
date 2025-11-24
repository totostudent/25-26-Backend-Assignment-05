package com.gdg.oauthgooglelogin.service;

import com.gdg.oauthgooglelogin.domain.Provider;
import com.gdg.oauthgooglelogin.domain.Role;
import com.gdg.oauthgooglelogin.domain.User;
import com.gdg.oauthgooglelogin.dto.TokenDto;
import com.gdg.oauthgooglelogin.dto.user.UserSignUpRequest;
import com.gdg.oauthgooglelogin.exception.CustomException;
import com.gdg.oauthgooglelogin.exception.ErrorCode;
import com.gdg.oauthgooglelogin.jwt.TokenProvider;
import com.gdg.oauthgooglelogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public TokenDto signUp(UserSignUpRequest userSignupRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //전에 저장해 놓은 인증된 객체 가져옴

        if (authentication != null && //인증 정보 존재하는 상태
                authentication.isAuthenticated() && //사용자가 이미 인증되어 있는지 확인
                !"anonymousUser".equals(authentication.getPrincipal())) { //anonymousUser는 '인증되지 않은 사용자'
            throw new CustomException(ErrorCode.ALREADY_LOGIN);
        }

        if (userRepository.existEmail(userSignupRequest.email())) { //이메일 중복 여부 확인
            throw new CustomException(ErrorCode.ALREADY_EXIST_EMAIL);
        }

        User user = userRepository.save(User.builder()
                .email(userSignupRequest.email())
                .password(passwordEncoder.encode(userSignupRequest.password()))
                .name(userSignupRequest.name())
                .provider(Provider.LOCAL)
                .role(Role.ROLE_USER)
                .build());

        return TokenDto.builder()
                .accessToken(tokenProvider.createAccessToken(user))
                .build();
    }

    public User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
