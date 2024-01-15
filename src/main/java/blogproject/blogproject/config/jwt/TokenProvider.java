package blogproject.blogproject.config.jwt;

import blogproject.blogproject.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider { // 토큰을 생성하고 올바른 토크인지 유효성 검사를 하고, 토큰에서 필요한 정보를 가져오는 클래스 파일

    private final JwtProperties jwtProperties;


    public String generateToken(User user, Duration expiredAt) { // Duration은 두 시간 사이의 간격을 나타냄
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user); // 현재 시간에 duration 객체를 밀리초로 변환한 값을 더함 -> 현재 시간에서 일정한 시간 간격을 더한 값을 얻음
    }


    // JWT 토근 생성 메서드
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ : jwt
                .setIssuer(jwtProperties.getIssuer()) // 내용 iss : ajufresh@gmail.com(properties 파일에서 설정한 값)
                .setIssuedAt(now) // 내용 iat : 현재 시간
                .setExpiration(expiry) // 내용 exp : expiry 멤버 변숫값
                .setSubject(user.getEmail()) // 내용 sub : 유저의 이메일
                .claim("id", user.getId()) // 클레임 id : 유저 id
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 서명 : 비밀 값과 함께 해시값을 HS256 방식으로 암호화
                .compact();
    }

    // JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) // 비밀값으로 복호화
                    .parseClaimsJws(token); // 주어진 jwt 토큰을 파싱하여 헤더+페이로드+서명으로 분리

            return true;
        } catch (Exception e) { // 복호화 과정에서 에러 발생 시 유효하지 않은 토큰
            return false;
        }
    }

    // 토큰 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        // 단 한 개의 객체만 저장 가능한 컬렉션
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")); // 권한 정보 생성 -> "ROLE_USER" 권한을 가진 SimpleGrantedAuthority 객체 생성

        // User 클래스는 spring security에서 제공하는 사용자 정보를 담는 클래스 -> (claims.getSubject() : jwt 토큰에서 추출한 서브젝트(주체) 정보를 가져와서 사용자 이름으로 설정, 패스워드에 빈 문자열 설정, authorities : 앞에서 생성한 권한 정보
        // UsernamePasswordAuthenticationToken : 사용자의 인증 정보와 권한 정보를 포함해서 spring security가 사용자를 식별하고 권한 부여함.
        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities), token, authorities);
    }

    // 토큰 기반으로 유저 아이디를 가져오는 메서드
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    // jwt를 decode해서 데이터를 얻어냄 -> 클레임 조회
    private Claims getClaims(String token) {
        return Jwts.parser() // jwt를 파싱하는 객체 생성
                .setSigningKey(jwtProperties.getSecretKey()) // 서명을 확인하기 위한 비밀키 설정
                .parseClaimsJws(token) // 전달받은 토큰을 파싱함 -> 서명이 유효한지 확인
                .getBody(); // jwt 본문을 반환함
    }


}
