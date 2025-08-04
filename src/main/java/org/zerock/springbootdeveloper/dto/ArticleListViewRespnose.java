package org.zerock.springbootdeveloper.dto;

import lombok.Getter;
import org.zerock.springbootdeveloper.domain.Article;

@Getter
public class ArticleListViewRespnose {
    private Long id;
    private String title;
    private String content;

    public ArticleListViewRespnose(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
