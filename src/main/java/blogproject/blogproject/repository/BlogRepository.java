package blogproject.blogproject.repository;

import blogproject.blogproject.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {
}
