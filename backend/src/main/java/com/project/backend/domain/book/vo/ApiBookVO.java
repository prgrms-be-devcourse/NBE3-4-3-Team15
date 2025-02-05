package com.project.backend.domain.book.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * -- Api 데이터를 받는 VO --
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

    @JsonCreator
    public ApiBookVO(
            @JsonProperty("title") String title,
            @JsonProperty("authors") List<String> kakaoAuthors,
            @JsonProperty("author") String naverAuthor,
            @JsonProperty("contents") String kakaoDescription,
            @JsonProperty("description") String naverDescription,
            @JsonProperty("thumbnail") String kakaoImage,
            @JsonProperty("image") String naverImage,
            @JsonProperty("isbn") String isbn
    ) {
        this.title = title;
        this.author = kakaoAuthors != null ? String.join(", ", kakaoAuthors) : (naverAuthor != null ? naverAuthor : "");
        this.description = kakaoDescription != null ? kakaoDescription : naverDescription;
        this.image = kakaoImage != null ? kakaoImage : naverImage;
        this.isbn = isbn;
    }
}
