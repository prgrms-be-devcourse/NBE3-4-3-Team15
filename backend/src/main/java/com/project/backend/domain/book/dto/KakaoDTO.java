package com.project.backend.domain.book.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.domain.book.util.BookUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * -- 카카오 데이터를 받는 DTO --
 *
 * @author -- 정재익 --
 * @since -- 2월 7일 --
 */
@ToString
@NoArgsConstructor
@Getter
@Setter
public class KakaoDTO {
    private String title;
    private String author;
    private String description;
    private String image;
    private String isbn;

    @JsonCreator
    public KakaoDTO(
            @JsonProperty("title") String title,
            @JsonProperty("authors") List<String> authors,
            @JsonProperty("contents") String description,
            @JsonProperty("thumbnail") String image,
            @JsonProperty("isbn") String isbn
    ) {
        this.title = title;
        this.author = String.join(", ", authors);
        this.description = description;
        this.image = image;
        this.isbn = BookUtil.extractIsbn(isbn);
    }
}