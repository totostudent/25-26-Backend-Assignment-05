package com.gdg.oauthgooglelogin.controller;

import com.gdg.oauthgooglelogin.domain.User;
import com.gdg.oauthgooglelogin.service.GoogleLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final GoogleLoginService googleLoginService;

    @GetMapping("/test") //구글 소셜 로그인된 계정 조회
    public User getUser(Principal principal) {
        return googleLoginService.test(principal);
    }
}
