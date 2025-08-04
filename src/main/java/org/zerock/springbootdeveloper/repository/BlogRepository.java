package org.zerock.springbootdeveloper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.springbootdeveloper.domain.Article;

public interface BlogRepository extends JpaRepository<Article,Long> {

}
