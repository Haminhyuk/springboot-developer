package org.zerock.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// 생성일, 수정일을 자동으로 설정하도록 하는 어노테이션
@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", updatable = false)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "author", nullable = false)
    private String author;

    // 데이터가 처음 저장될때의 날짜 및 시간을 저장
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    // 데이터가 마지막에 수정된 날짜 및 시간을 자동 저장
    @LastModifiedDate
    @Column(name="updated_at")
    private LocalDateTime updatedAt;




    @Builder
    public Article(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }
    public void update(String title, String content) {
        this.title = title;
        this.content = content;

    }

}
