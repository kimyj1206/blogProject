package blogproject.blogproject.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User implements UserDetails { // UserDetails를 상속받아 인증 객체로 사용, 스프링 시큐리티에서 사용자의 인증 정보를 담아 두는 인터페이스

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // pk 생성 규칙 -> id 값을 null로 하면 db가 auto_increment 함.
    @Column(name = "id", updatable = false) // 객체 필드를 테이블과 매핑 name = 필드와 매핑할 테이블의 컬럼 이름을 지정한다. / updatable = 엔티티 수정 시 이 필드도 같이 수정한다.
    private Long id;

    @Column(name = "email", nullable = false, unique = true) // nullable = null 값의 허용 여부를 설정한다. false 설정 시 not null 제약조건이
    private String email;

    @Column(name = "password")
    private String password;

    @Builder
       public User(String email, String password, String auth) {
        this.email = email;
        this.password = password;
    }

    @Override
    // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() { // 사용자가 가지고 있는 권한의 목록을 반환함 -> 현재 코드에서는 사용자 이외의 권한이 없음, user 권한만 담아 반환
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    // 사용자의 고유한 값(아이디) 반환
    public String getUsername() {
        return email;
    }

    @Override
    // 사용자의 패스워드 반환
    public String getPassword() {
        return password;
    }

    @Override
    // 계정 만료 여부 반환 - 만료되었는지 확인하는 로직
    public boolean isAccountNonExpired() {
        return true; // 만료되지 않았음
    }

    @Override
    // 계정 잠금 여부 반환 - 계정 잠금되었는지 확인하는 로직
    public boolean isAccountNonLocked() {
        return true; // 잠금되지 않았음
    }

    @Override
    // 패스워드 만료 여부 반환 - 패스워드가 만료되었는지 확인하는 로직
    public boolean isCredentialsNonExpired() {
        return true; // 만료되지 않았음
    }

    @Override
    // 계정 사용 가능 여부 반환 - 계정이 사용 가능한지 확인하는 로직
    public boolean isEnabled() {
        return true; // 사용 가능
    }
}