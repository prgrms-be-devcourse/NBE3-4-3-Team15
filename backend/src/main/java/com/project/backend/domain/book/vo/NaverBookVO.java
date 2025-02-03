package com.project.backend.domain.book.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.domain.book.entity.Book;
import lombok.*;

import java.util.List;

/**
 * -- 네이버 api와 통신하기 위한 클래스 --
 * 조회값을 받아와 읽기전용으로만 쓰기 때문에 DTO대신 VO로 설정했다.
 *
 * @author -- 정재익 --
 * @since -- 1월 31일 --
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NaverBookVO {

    @JsonProperty("items")
    private List<Book> items;
}