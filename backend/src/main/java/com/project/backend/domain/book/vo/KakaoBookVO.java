package com.project.backend.domain.book.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import java.util.List;

/**
 * -- 카카오 도서 API에서 받은 응답 데이터를 표현하는 VO --
 * 카카오 API의 응답으로 도서 목록이 포함된 documents 필드를 포함
 *
 * @author 김남우
 * @since 2025년 1월 27일
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KakaoBookVO {

    @JsonProperty("documents")
    private List<Item> items;

    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Item {
        @JsonProperty("title")
        private String title;

        @JsonProperty("authors")
        private List<String> authors;
        @JsonProperty("contents")
        private String description;

        @JsonProperty("thumbnail")
        private String image;

        @JsonProperty("isbn")
        private String isbn;

        public String getAuthor() {
            return String.join(", ", authors); // ["작가1", "작가2"] → "작가1, 작가2"
        }
    }
}