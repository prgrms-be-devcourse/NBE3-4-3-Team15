package com.project.backend.domain.book.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@ToString
@NoArgsConstructor
@Getter
@Setter
public class BookVO {

    private String title;
    private String author;      // 카카오의 authors(배열)를 문자열로 변환해서 저장
    private String description;
    private String image;
    private String isbn;

    // ⭐ 카카오 API 응답을 BookVO로 변환하는 생성자
    public BookVO(
            @JsonProperty("title") String title,
            @JsonProperty("authors") List<String> authors,
            @JsonProperty("contents") String description,
            @JsonProperty("thumbnail") String image,
            @JsonProperty("isbn") String isbn
    ) {
        this.title = title;
        this.author = authors != null ? String.join(", ", authors) : ""; // 배열 → 문자열 변환
        this.description = description;
        this.image = image;
        this.isbn = isbn;
    }

    // ⭐ 네이버 API 응답을 BookVO로 변환하는 생성자
    public BookVO(
            @JsonProperty("title") String title,
            @JsonProperty("author") String author,
            @JsonProperty("description") String description,
            @JsonProperty("image") String image,
            @JsonProperty("isbn") String isbn
    ) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.image = image;
        this.isbn = isbn;
    }
}
