package org.zerock.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.springbootdeveloper.domain.Article;
import org.zerock.springbootdeveloper.dto.ArticleListViewRespnose;
import org.zerock.springbootdeveloper.dto.ArticleResponse;
import org.zerock.springbootdeveloper.dto.ArticleViewResponse;
import org.zerock.springbootdeveloper.service.BlogService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogService blogService;
//   전체 데이터 검색
    @GetMapping("/articles")
    public String getArticles(Model model){

        List<ArticleListViewRespnose> articles = blogService.findAll().stream()
                .map(ArticleListViewRespnose::new)
                .toList();
        model.addAttribute("articles", articles);


        return "articleList";
    }

//  전체 데이터 중 1개 검색
    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model){
        Article article = blogService.findById(id);
        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }

    @GetMapping("/new-article")
    //@RequestParam(required = false) : 파라미터에 id가 없어도 실행되도록 설정
    public String newArticle(@RequestParam(required = false) Long id, Model model){
        // 새로운 Blog 데이터 추가 페이지
        if(id == null){
            model.addAttribute("article", new ArticleViewResponse());
        }else{
            // id가 있을 경우 데이터를 찾아서 돌려줌 : 수정 페이지
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }
        return "newArticle";
    }

}
