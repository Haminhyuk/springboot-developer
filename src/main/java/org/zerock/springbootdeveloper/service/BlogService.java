package org.zerock.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.springbootdeveloper.domain.Article;
import org.zerock.springbootdeveloper.dto.AddArticleRequest;
import org.zerock.springbootdeveloper.dto.UpdateArticleRequest;
import org.zerock.springbootdeveloper.repository.BlogRepository;

import java.util.List;


@RequiredArgsConstructor
@Service
public class BlogService {
    private final BlogRepository blogRepository;


    // 블로그 글 추가 메서드
    public Article save(AddArticleRequest request, String userName){

        return blogRepository.save(request.toEntity(userName));
    }

    // DB에 저장되어있는 모든 데이터를 조회
    public List<Article> findAll(){
        return blogRepository.findAll();
    }
    
    // DB의 데이터 한 건 조회
    public Article findById(Long id){
        // 조회 결과가 없을때  예외처리를 실행
        return blogRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("not found " + id));
    }
    // 데이터 삭제
    public void delete(Long id , String username){
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found " + id));

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    // 데이터 수정하기
    @Transactional
    public Article update(long id, UpdateArticleRequest request){
        // id를 이용하여 데이터를 변수에 저장
        Article article = blogRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("not found" + id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());
        // @Transactional이 없다면 아래와 같이 save를 실행해야함
        //blogRepository.save(article);
        return article;
    }

    private void authorizeArticleAuthor(Article article) {
        // 현재 로그인된 계정의 이름을 변수에 저장
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        // 작성자와 아이디가 같은지 확인
        if (!article.getAuthor().equals(userName)){
            throw new IllegalArgumentException("not authorized");
        }
    }
}
