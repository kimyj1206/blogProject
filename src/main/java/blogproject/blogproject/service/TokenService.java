package blogproject.blogproject.service;

import blogproject.blogproject.config.jwt.TokenProvider;
import blogproject.blogproject.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    // 전달받은 리프레시 토큰으로 유효성 검사를 진행하고, 유효한 토큰일 때 리프레시 토큰으로 사용자 아이디를 찾음
    // 사용자 아이디로 사용자를 찾은 후에 토큰 제공자의 generateToken() 메서드로 새로운 액세스 토큰을 생성함.
    public String createdNewAccessToken(String refreshToken) {
        // 토큰 유효성 검사에 실패하면 예외 발생
        if(!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
