package com.project.backend.domain.book.controller;

import com.project.backend.domain.book.dto.BookSimpleDto;
import com.project.backend.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService bookService;

    /**
     * -- 제목 검색 --
     *
     * @param -- title(검색어) --
     * @return -- BookSimpleDto --
     * @author -- 정재익 --
     * @since -- 1월 25일 --
     */

    @GetMapping("/book")
    public List<BookSimpleDto> searchTitleBooks(@RequestParam("title") String title) {
        return bookService.searchTitleBooks(title);
    }


    /**
     * -- 도서 조회 --
     *
     * @return -- List<BookSimpleDto> --
     * 많은 책을 간략하게 보여줄때는 BooksimpleDto를 사용하고 책 세부 내용을 보여줄 때는 BookDto를 사용
     * @author -- 정재익 --
     * @since -- 1월 25일 --
     */

    @GetMapping("/book/list")
    public List<BookSimpleDto> searchAllBooks() {
        return bookService.searchAllBooks();
    }
}
