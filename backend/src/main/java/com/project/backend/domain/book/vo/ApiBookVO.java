package com.project.backend.domain.book.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.domain.book.util.BookUtil;
import io.micrometer.common.util.StringUtils;
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
        this.author = StringUtils.isBlank(naverAuthor) ? String.join(", ", kakaoAuthors) : naverAuthor;
        this.description = StringUtils.isBlank(naverDescription) ? kakaoDescription : naverDescription;
        this.image = StringUtils.isBlank(naverImage) ? kakaoImage : naverImage;
        this.isbn = BookUtil.extractIsbn(isbn);
    }
}
