package com.project.backend.domain.book.controller;

import com.project.backend.domain.book.dto.BookDto;
import com.project.backend.domain.book.dto.BookSimpleDto;
import com.project.backend.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    /**
     * -- 제목 검색 --
     * 네이버 api의 정보를 바탕으로 제목을 검색 함 검색한 데이터는 DB에 저장 됨
     *
     * @param -- title(검색어) --
     * @return -- BookSimpleDto --
     * @author -- 정재익 --
     * @since -- 1월 27일 --
     */
    @GetMapping
    public List<BookSimpleDto> searchTitleBooks(@RequestParam("title") String title) {
        return bookService.searchTitleBooks(title);
    }


    /**
     * -- 도서 조회 --
     *
     * @return -- List<BookSimpleDto> --
     * DB에 있는 책을 토대로 도서를 조회함
     * 많은 책을 간략하게 보여줄때는 BooksimpleDto를 사용하고 책 세부 내용을 보여줄 때는 BookDto를 사용
     * @author -- 정재익 --
     * @since -- 1월 27일 --
     */
    @GetMapping("/list")
    public List<BookSimpleDto> searchAllBooks() {
        return bookService.searchAllBooks();
    }

    /**
     * -- 도서 상세 조회 --
     * DB에 있는 책의 상세정보를 조회함 Favorite서비스에서 책이 추천받은 수를 받아와 DB에 저장후 반환
     *
     * @param -- 책의 id --
     * @return -- BookDto --
     * @author -- 정재익 --
     * @since -- 1월 27일 --
     */
    @GetMapping("/{id}")
    public BookDto searchDetailBook(@PathVariable("id") int id) {
        return bookService.searchDetailsBook(id);
    }
}
