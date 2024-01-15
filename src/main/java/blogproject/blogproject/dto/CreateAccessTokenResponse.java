package blogproject.blogproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateAccessTokenResponse { // 토큰 생성 응답

    private String accessToken;
}
