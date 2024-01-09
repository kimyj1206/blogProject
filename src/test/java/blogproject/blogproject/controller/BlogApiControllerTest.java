package blogproject.blogproject.controller;

import blogproject.blogproject.domain.Article;
import blogproject.blogproject.dto.AddArticleRequest;
import blogproject.blogproject.dto.UpdateArticleRequest;
import blogproject.blogproject.repository.BlogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // 테스트용 애플리케이션 컨텍스트
@AutoConfigureMockMvc // MockMvc 생성 및 자동 구성
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc; // 컨트롤러 테스트를 할 수 있는 모의 환경 도구

    @Autowired
    protected ObjectMapper objectMapper; // 직렬화, 역직렬화를 위한 클래스 -> 직렬화 : 자바 객체를 JSON 데이터 변환, 역직렬화 : JSON 데이터를 자바 객체 변환

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    BlogRepository blogRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext) // 웹 애플리케이션과 유사한 환경을 제공하는 초기화 코드
                .build();
        blogRepository.deleteAll();
    }

    @DisplayName("addArticle : 블로그 글 추가에 성공한다.")
    @Test
    public void addArticle() throws Exception {
        // given
        final String url = "/api/articles";
        final String title = "title1";
        final String content = "content1";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        // writeValueAsString() 사용해서 객체 -> JSON 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        // when
        // 설정 내용을 바탕으로 요청 전송
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("findAllArticles : 블로그 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception {
        // given : 블로그 글을 저장한다.
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";

        blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        // when : 목록 조회 api 호출한다.
        final ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // then : 응답 코드가 200 ok, 반환 값 중 0번째 요소의 content, title이 저장된 값과 같은지 확인한다.
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(content))
                .andExpect(jsonPath("$[0].title").value(title));
    }

    @DisplayName("findArticle : 블로그 글 조회에 성공한다")
    @Test
    public void findArticle() throws Exception {
        // given : 블로그 글을 저장한다.
        final String url = "/api/articles/{id}";
        final String title = "title";
        final String content = "content";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        // when : 저장한 블로그 글의 id 값으로 api 호출한다.
        final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));

        // then : 응답 코드가 200 ok, 반환받은 content, title이 저장된 값과 같은지 확인한다.
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.title").value(title));
    }

    @DisplayName("deleteArticle : 블로그 글 삭제에 성공한다")
    @Test
    public void deleteArticle() throws Exception {
        // given : 블로그 글을 저장한다.
        final String url = "/api/articles/{id}";
        final String title = "title";
        final String content = "content";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        // when : 저장한 블로그 글의 id 값으로 삭제 api 호출한다.
        mockMvc.perform(delete(url, savedArticle.getId()))
                .andExpect(status().isOk());

        // then : 응답 코드가 200 ok, 블로그 글 리스트를 전체 조회해 배열 크기가 0인지 확인한다.
        List<Article> articles = blogRepository.findAll();

        assertThat(articles).isEmpty();
    }

    @DisplayName("updatedArticle : 블로그 글 수정에 성공한다.")
    @Test
    public void updatedArticle() throws Exception {
        // given : 블로그 글을 저장하고, 블로그 수정에 필요한 객체 생성
        final String url = "/api/articles/{id}";
        final String title = "title";
        final String content = "content";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        final String newTitle = "new title";
        final String newContent = "new content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        // when : update api로 요청 전달, 요청 타입은 json, given 절에서 만들어둔 객체를 요청 본문에 함께 전달
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        // then : 응답 코드가 200 ok, 블로그 글 id로 조회한 후 값이 수정되었는지 확인
        result.andExpect(status().isOk());

        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
    }

}