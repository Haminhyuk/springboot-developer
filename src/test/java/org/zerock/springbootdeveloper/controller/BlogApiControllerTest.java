package org.zerock.springbootdeveloper.controller;

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
import org.zerock.springbootdeveloper.domain.Article;
import org.zerock.springbootdeveloper.dto.AddArticleRequest;
import org.zerock.springbootdeveloper.dto.UpdateArticleRequest;
import org.zerock.springbootdeveloper.repository.BlogRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @BeforeEach
    public void setupMockMvc() {
        // Controller를 실행할때 MockMvc 설정
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        blogRepository.deleteAll();
    }

    @DisplayName("addArticle: 블로그 글 추가에 성공한다.")
    @Test
    public void addArticle() throws Exception {
//    given : 데이터 및 사전 준비
        final String url = "/api/articles"; // 실행할 컨트롤러 주소
        final String title = "title"; // 저장할 데이터 1
        final String content = "content"; // 저장할 데이터 2
        // Post 실행시 전달할 객체
        final AddArticleRequest userRequest = new AddArticleRequest(title, content, "hong");
        // 객체(userRequest)를 json형식의 문자열로 변경
        final String requestBody = objectMapper.writeValueAsString(userRequest);

//      when : 컨트롤러 실행 및 결과 저장
        ResultActions result = mockMvc.perform(post(url)
//                RestController는 JSON이 기본 데이터 형식이기 때문에 JSON으로 설정
                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                실제 데이터 저장
                .content(requestBody));


        //then : 결과가 예상과 일치하는지 확인
        // 통신코드가 201 created가 맞는지 확인
        result.andExpect(status().isCreated());
        // 저장한 DB의 모든 데이터를 저장
        List<Article> articles = blogRepository.findAll();
        // 저장된 데이터가 1개가 맞는지 확인
        assertThat(articles.size()).isEqualTo(1);
        // title 데이터가 일치하는지 확인
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        // content 데이터가 일치하는지 확인
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("findAllArticle: 블로그 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticle() throws Exception {
//    given : 데이터 및 사전 준비
        final String url = "/api/articles"; // 실행할 컨트롤러 주소
        final String title = "title"; // 저장할 데이터 1
        final String content = "content"; // 저장할 데이터 2

        // 새로운 데이터 저장
        blogRepository.save(Article.builder()
                        .title(title)
                        .content(content)
                        .build());

//      when : 컨트롤러 실행 및 결과 확인
        final ResultActions resultActions = mockMvc.perform(get(url)
//                RestController는 JSON이 기본 데이터 형식이기 때문에 JSON으로 설정
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(content))
                .andExpect(jsonPath("$[0].title").value(title));
    }

    @DisplayName("findAllArticle: 블로그 글 조회에 성공한다.")
    @Test
    public void findArticle() throws Exception {
//    given : 데이터 및 사전 준비
        final String url = "/api/articles/{id}"; // 실행할 컨트롤러 주소
        final String title = "title"; // 저장할 데이터 1
        final String content = "content"; // 저장할 데이터 2

        // 새로운 데이터 저장
        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

//      when : 컨트롤러 실행 및 결과 확인
        final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));


        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.title").value(title));
    }

    @DisplayName("findAllArticle: 블로그 글 삭제에 성공한다.")
    @Test
    public void deleteArticle() throws Exception {
//    given : 데이터 및 사전 준비
        final String url = "/api/articles/{id}"; // 실행할 컨트롤러 주소
        final String title = "title"; // 저장할 데이터 1
        final String content = "content"; // 저장할 데이터 2

        // 새로운 데이터 저장
        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

//      when : 컨트롤러 실행 및 결과 확인
        // 위에서 저장한 article 삭제 컨트롤러 실행
        mockMvc.perform(delete(url, savedArticle.getId())).andExpect(status().isOk());
//        final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));

//        결과 확인
        List<Article> articles = blogRepository.findAll();

        assertThat(articles).isEmpty();
    }
    @DisplayName("updateArticle: 블로그 글 수정에 성공한다.")
    @Test
    public void updateArticle() throws Exception {
        // given : 데이터 및 사전 준비
        final String url = "/api/articles/{id}"; // 실행할 컨트롤러 주소
        final String title = "title"; // 저장할 데이터 1
        final String content = "content"; // 저장할 데이터 2

        // 새로운 데이터 저장
        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        final String newTitle = "new Title";
        final String newContent = "new Content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);
        // when
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk());

        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);

    }
}