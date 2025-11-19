package com.gdg.oauthgooglelogin.dto.user;

public record UserSignUpRequest (
    String email,
    String password,
    String name
){}
