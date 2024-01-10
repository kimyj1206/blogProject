package blogproject.blogproject.config;

import blogproject.blogproject.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailService userDetailService;


    // spring  security 기능 비활성화 -> spring security 모든 기능을 사용하지 않겠음을 설정, 인증/인가 서비스를 모든 곳에 적용하지는 않는다.
    // 일반적으로 정적 리소스에 설정하는데 해당 코드에서는 static 하위 경로에 있는 리소스와 h2의 데이터를 확인하는데 사용하는 h2-console 하위 url을 제외함.
    @Bean
    public WebSecurityCustomizer configure() {
        return (web -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/static/**")
        );
    }


    // 특정 http 요청에 대한 웹 기반 보안 구성 - 인증/ 인가 및 로그인, 로그아웃 관련 설정 가능.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests() // 인증, 인가 설정 - 특정 경로에 대한 액세스 설정을 함.
                    .requestMatchers("/login", "/signup", "/user").permitAll() // 해당 경로에 들어온 요청만 모든 사용자가 인증 없이도 접근 가능함.
                    .anyRequest().authenticated()
                .and()
                .formLogin() // form 기반 로그인 설정
                    .loginPage("/login")
                    .defaultSuccessUrl("/articles")
                .and()
                .logout() // 로그아웃 설정
                    .logoutSuccessUrl("/login")
                    .invalidateHttpSession(true)
                .and()
                .csrf().disable() // csrf 비활성화
                .build();
    }

    // 인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailService) // 사용자 정보 서비스 설정
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }


    // 패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}