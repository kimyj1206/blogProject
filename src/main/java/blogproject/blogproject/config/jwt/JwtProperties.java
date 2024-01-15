package blogproject.blogproject.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt") // 자바 클래스에 프로퍼티 값을 가져와서 사용하는 애너테이션
public class JwtProperties { // application.yml 파일에서 설정한 값들을 변수로 접근하는데 사용할 클래스 파일

    // application.yml에서 지정한 토큰 값을 못 받아와서 명시적으로 값 지정
    @Value("${spring.jwt.issuer}")
    private String issuer;

    @Value("${spring.jwt.secret}")
    private String secretKey;
}
