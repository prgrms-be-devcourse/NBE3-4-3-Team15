package com.project.backend.domain.book.controller;

import com.project.backend.domain.book.service.BookService;
import com.project.backend.domain.book.vo.NaverBookVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService bookService;

    /**
     * -- 검색어로 관련책을 검색하는 메소드 --
     *
     * @param -- query(검색어) --
     * @return -- NaverBookVo --
     *
     * @author -- 정재익 --
     * @since -- 1월 24일 --
     */
    @GetMapping("/book")
    public NaverBookVo searchBooks(@RequestParam(value = "title") String title) {
        return bookService.searchBooks(title);
    }
}
