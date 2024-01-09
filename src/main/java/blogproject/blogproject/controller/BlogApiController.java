package blogproject.blogproject.controller;

import blogproject.blogproject.domain.Article;
import blogproject.blogproject.dto.AddArticleRequest;
import blogproject.blogproject.dto.ArticleResponse;
import blogproject.blogproject.dto.UpdateArticleRequest;
import blogproject.blogproject.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // http response body에 객체 데이터를 json 형식으로 반환함.
@RequiredArgsConstructor  // final이 붙거나 @NotNull이 붙은 필드의 생성자를 자동으로 추가함.
public class BlogApiController {

    private final BlogService blogService;

    // 글 추가
    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request) { // @RequestBody로 요청 본문 값 매핑
        Article savedArticle = blogService.save(request);

        // 요청한 자원이 성공적으로 생성되었으며, 저장된 블로그 글 정보를 응답 객체에 담아 전송
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle);
    }

    // 전체 글 조회
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream() // 원본 데이터를 변경하지 않고 데이터를 다루기 위한 일련의 연속된 연산 제공
                .map(ArticleResponse::new) // 각 요소를 ArticleResponse 객체로 변환해 새로운 스트림 생성
                .toList(); // 리스트로 변환

        return ResponseEntity.ok()
                .body(articles); // 응답용 객체인 ArticleResponse로 파싱해 body에 담아 클라이언트에게 전달
    }

    // 단건 글 조회
    @GetMapping("/api/articles/{id}")
    // url 경로에서 값 추출
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        Article article = blogService.findById(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    // 글 삭제
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        blogService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    // 글 수정
    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody UpdateArticleRequest request) {
        Article updatedArticle = blogService.update(id, request);

        return ResponseEntity.ok()
                .body(updatedArticle);
    }
}
