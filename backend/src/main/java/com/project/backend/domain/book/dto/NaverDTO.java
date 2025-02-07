package com.project.backend.domain.book.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * -- 네이버 데이터를 받는 DTO --
 *
 * @author -- 정재익 --
 * @since -- 2월 7일 --
 */
@ToString
@NoArgsConstructor
@Getter
@Setter
public class NaverDTO {
    private String title;
    private String author;
    private String description;
    private String image;
    private String isbn;

    @JsonCreator
    public NaverDTO(
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
