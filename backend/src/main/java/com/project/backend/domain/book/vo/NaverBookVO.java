package com.project.backend.domain.book.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * -- 네이버 api와 통신하기 위한 클래스 --
 * 조회값을 받아와 읽기전용으로만 쓰기 때문에 DTO대신 VO로 설정했다.
 * 네이버 api의 리턴값이 중첩된 JSON구조로 되어있기 때문에 내부클래스를 이용해서 중첩구조로 구현했다.
 *
 * @author -- 정재익 --
 * @since -- 1월 25일 --
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NaverBookVO {

    @JsonProperty("items")
    private List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Item {
        @JsonProperty("title")
        private String title;

        @JsonProperty("image")
        private String image;

        @JsonProperty("author")
        private String author;

        @JsonProperty("description")
        private String description;

        @JsonProperty("isbn")
        private String isbn;
    }
}

