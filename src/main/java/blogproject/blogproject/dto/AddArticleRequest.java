package blogproject.blogproject.dto;

import blogproject.blogproject.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
public class AddArticleRequest {

    private String title;
    private String content;

    // 빌더 패턴을 사용해 Article 객체 생성, DTO를 Entity로 만들어주는 메서드로 추후 블로그 글 추가할 때 저장할 엔티티로 변환하는 용도
    public Article toEntity() {
        return Article.builder() // Article 클래스에서 @Builder가 생성한 빌더 패턴을 가져와 사용함
                .title(title) // title 세팅
                .content(content) // content 세팅
                .build(); // 위에 설정된 값을 토대로 최종적으로 객체 생성해줌.
    }
}
