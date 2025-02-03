package com.project.backend.domain.book.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

/**
 * -- 책 엔티티 --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("title")
    private String title;

    @Column(length = 100)
    @JsonProperty("author")
    private String author;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("description")
    private String description;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("image")
    private String image;

    @Column(length = 50, unique = true, nullable = false)
    @JsonProperty("isbn")
    private String isbn;

    private int favoriteCount;
}