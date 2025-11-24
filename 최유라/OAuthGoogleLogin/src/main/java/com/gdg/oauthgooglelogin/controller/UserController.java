package com.gdg.oauthgooglelogin.controller;

import com.gdg.oauthgooglelogin.dto.TokenDto;
import com.gdg.oauthgooglelogin.dto.user.UserSignUpRequest;
import com.gdg.oauthgooglelogin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<TokenDto> signUp(@RequestBody UserSignUpRequest userSignUpRequest) {
        return ResponseEntity.created(URI.create("/user/")).body(userService.signUp(userSignUpRequest));
    }
}
