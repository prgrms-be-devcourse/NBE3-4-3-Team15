package com.project.backend.domain.book.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * -- 카카오, 네이버 Api 데이터를 받는 VO --
 *
 * @author -- 정재익 --
 * @since -- 2월 5일 --
 */
@ToString
@NoArgsConstructor
@Getter
@Setter
public class ApiBookVO {

    private String title;
    private String author;
    private String description;
    private String image;
    private String isbn;

    // 카카오 API 응답을 BookVO로 변환하는 생성자
    public ApiBookVO(
            @JsonProperty("title") String title,
            @JsonProperty("authors") List<String> authors,
            @JsonProperty("contents") String description,
            @JsonProperty("thumbnail") String image,
            @JsonProperty("isbn") String isbn
    ) {
        this.title = title;
        this.author = authors != null ? String.join(", ", authors) : "";
        this.description = description;
        this.image = image;
        this.isbn = isbn;
    }

    // ⭐ 네이버 API 응답을 BookVO로 변환하는 생성자
    public ApiBookVO(
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
