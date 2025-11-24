package com.gdg.oauthgooglelogin.controller;

import com.gdg.oauthgooglelogin.domain.User;
import com.gdg.oauthgooglelogin.dto.TokenDto;
import com.gdg.oauthgooglelogin.service.GoogleLoginService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/api/oauth2")
public class AuthController {

    private final GoogleLoginService googleLoginService;

    @GetMapping("/callback/google")
    public TokenDto googleCallback(@RequestParam String code, @Value("${spring.security.oauth2.client.registration.google.client-id}") String ID,
                                   @Value("${spring.security.oauth2.client.registration.google.client-secret}") String secret) {
        String googleAccessToken = googleLoginService.getGoogleAccessToken(code, ID, secret);
        return loginOrSignup(googleAccessToken);
    }

    private TokenDto loginOrSignup(String googleAccessToken) {
        return googleLoginService.loginOrSignUp(googleAccessToken);
    }

    @GetMapping("/test") //구글 소셜 로그인된 계정 조회
    public User getUser(Principal principal) {
        return googleLoginService.test(principal);
    }
}
