package com.project.backend.domain.book.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NaverBookVo {

    /**
     * -- 네이버 api와 통신하기 위한 VO --
     * 조회값을 받아와 읽기전용으로만 쓰기 때문에 DTO대신 VO로 설정했다.
     * 네이버 api의 리턴값이 중첩된 JSON구조로 되어있기 때문에 내부클래스를 이용해서 중첩구조로 구현했다.
     * description은 검색조회에 보여지지않도록 어노테이션을 통해 접근을 제어했다.
     *
     * @author -- 정재익 --
     * @since -- 1월 25일 --
     */

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

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private String description;
    }
}

