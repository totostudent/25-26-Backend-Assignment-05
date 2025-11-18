package com.gdg.oauthgooglelogin.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfo { //OAuth에서 쓸 user 정보?(service 계층에서 사용한다)

    private String id;
    private String email;

    @SerializedName("verified_email") //필드 이름 원하는 이름으로 변경(verified_email 프로퍼티 생성)
    private Boolean verifiedEmail;

    private String name;

    @SerializedName("given_name")
    private String givenName; //이름

    @SerializedName("family_name")
    private String familyName; //성

    @SerializedName("picture") //구글이 내려주는 JSON 키 이름이 picture 이라서 그에 맞춰 매핑해야 함?
    private String pictureUrl;

    private String locale; //지역
}
