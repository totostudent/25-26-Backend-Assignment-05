package com.gdg.oauthgooglelogin.jwt;

import com.gdg.oauthgooglelogin.domain.User;
import com.gdg.oauthgooglelogin.exception.CustomException;
import com.gdg.oauthgooglelogin.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    private static final String ROLE_CLAIM = "Role";
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";

    private final Key key;
    private final long accessTokenValidityTime;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         @Value("${jwt.access-token-validity-in-milliseconds}") long accessTokenValidityTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityTime = accessTokenValidityTime;
    }

    public String createAccessToken(User User) {
        long nowTime = (new Date().getTime());

        Date accessTokenExpiredTime = new Date(nowTime + accessTokenValidityTime);

        return Jwts.builder()
                .setSubject(User.getId().toString()) //토큰 제목(유저 이름)
                .claim(ROLE_CLAIM, User.getRole().name()) //권한 정보
                .setExpiration(accessTokenExpiredTime) //토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) //HS256은 대칭키? 기반 SHA-256 해시? 만들어 서명하는 알고리즘
                .compact(); //최종 jwt 문자열 생성?
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(ROLE_CLAIM) == null) {
            throw new CustomException(ErrorCode.NO_ROLE_TOKEN); //권한 정보 없는 토큰
        }

        // 사용자의 권한 정보를 securityContextHolder에 담아준다
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(ROLE_CLAIM).toString().split(","))
                        // 해당 hasRole이 권한 정보를 식별하기 위한 전처리 작업..?
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);
        authentication.setDetails(claims);

        return authentication;
    }

    public String resolveToken(HttpServletRequest request) { //토큰 분해/분석
        String bearerToken = request.getHeader(AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(7); //"Bearer "길이만큼 빼버림
        }
        return null;
    }

    public boolean validateToken(String token) {
        try { //좀 더 읽어봐야겠다
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | SignatureException | MalformedJwtException e) {
            // 서명 불일치/위변조/형식 불량
            return false;
        } catch (ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) { //JWT관련 예외들?
            return false;
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (SignatureException e) {
            throw new CustomException(ErrorCode.CANNOT_USE_TOKEN); //토큰 복호화 실패
        }
    }
}
