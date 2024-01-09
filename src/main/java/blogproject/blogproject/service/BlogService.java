package blogproject.blogproject.service;

import blogproject.blogproject.domain.Article;
import blogproject.blogproject.dto.AddArticleRequest;
import blogproject.blogproject.dto.UpdateArticleRequest;
import blogproject.blogproject.repository.BlogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final이 붙거나 @NotNull이 붙은 필드의 생성자를 자동으로 추가함.
public class BlogService {

    private final BlogRepository blogRepository;

    // blog 글 추가
    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }

    // blog 전체 글 조회
    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    // blog 단건 조회
    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id)); // Optional 객체가 비어있을 때 예외를 던짐
    }

    // blog 글 삭제
    public void delete(long id) {
        blogRepository.deleteById(id);
    }

    // blog 글 수정
    @Transactional // 트랜잭션 메서드
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        article.update(request.getTitle(), request.getContent());

        return article;
    }
}