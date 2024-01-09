package blogproject.blogproject;

import blogproject.blogproject.domain.Article;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // created_at, updated_at 자동 업데이트
@SpringBootApplication
public class BlogProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogProjectApplication.class, args);

        }
}