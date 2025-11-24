package com.gdg.oauthgooglelogin.service;

import com.gdg.oauthgooglelogin.domain.Provider;
import com.gdg.oauthgooglelogin.exception.CustomException;
import com.gdg.oauthgooglelogin.exception.ErrorCode;
import com.gdg.oauthgooglelogin.jwt.TokenProvider;
import com.gdg.oauthgooglelogin.repository.UserRepository;
import com.google.gson.Gson;
import com.gdg.oauthgooglelogin.domain.Role;
import com.gdg.oauthgooglelogin.domain.User;
import com.gdg.oauthgooglelogin.dto.TokenDto;
import com.gdg.oauthgooglelogin.dto.user.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleLoginService {

    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private final String GOOGLE_REDIRECT_URI = "http://localhost:8080/api/callback/google";

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public String getGoogleAccessToken(String code, @Value("${spring.security.oauth2.client.registration.google.client-id}") String ID,
                                       @Value("${spring.security.oauth2.client.registration.google.client-secret}") String secret) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = Map.of(
                "code", code,
                "scope", "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email",
                "client_id", ID,
                "client_secret", secret,
                "redirect_uri", GOOGLE_REDIRECT_URI,
                "grant_type", "authorization_code"
        );

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_URL, params, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) { //요청 성공 시
            String json = responseEntity.getBody(); //받은 값?의 body부분을 json에 저장?
            Gson gson = new Gson();

            return gson.fromJson(json, TokenDto.class)
                    .getAccessToken();
        }

        throw new CustomException(ErrorCode.FAILED_TO_TAKE_TOKEN); //토큰 가져오기 실패
    }

    public TokenDto loginOrSignUp(String googleAccessToken) {
        UserInfo userInfo = getUserInfo(googleAccessToken);

        if (!userInfo.getVerifiedEmail()) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VARIFIED); //이메일 인증 안 됨
        }

        User user = userRepository.findByEmail(userInfo.getEmail()) //이메일로 소셜 로그인 시도
                .orElseGet(() -> userRepository.save(User.builder() //처음 로그인하는 경우?
                        .email(userInfo.getEmail())
                        .name(userInfo.getName())
                        .provider(Provider.GOOGLE)
                        .profileUrl(userInfo.getPictureUrl())
                        .role(Role.ROLE_USER)
                        .build())
                ); //사용자 정보 저장

        return TokenDto.builder()
                .accessToken(tokenProvider.createAccessToken(user)) //accesstoken 발급해주기
                .build();
    }

    private UserInfo getUserInfo(String accessToken) { //유저 정보 가져오기
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON); //json 형식으로 요청/응답 보낸다

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();
            return gson.fromJson(json, UserInfo.class);
        }

        throw new CustomException(ErrorCode.FAILED_TO_GET_USER); //유저 정보 가져오기 실패
    }

    public User test(Principal principal) { //유저 조회?
        Long id = Long.parseLong(principal.getName());

        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CANNOT_FIND_USER)); //유저가 존재하지 않음
    }
}
