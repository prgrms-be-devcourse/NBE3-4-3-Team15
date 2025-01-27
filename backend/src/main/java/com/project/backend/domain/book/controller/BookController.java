package com.project.backend.domain.book.controller;

import com.project.backend.domain.book.dto.BookDTO;
import com.project.backend.domain.book.dto.BookSimpleDTO;
import com.project.backend.domain.book.dto.FavoriteDTO;
import com.project.backend.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public List<BookSimpleDTO> searchTitleBooks(@RequestParam("title") String title) {
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
    public List<BookSimpleDTO> searchAllBooks() {
        return bookService.searchAllBooks();
    }

    /**
     * -- 도서 상세 조회 --
     * DB에 있는 책의 상세정보를 조회함 Favorite서비스에서 책이 추천받은 수를 받아와 DB에 저장후 반환
     *
     * @return -- BookDto --
     * @author -- 정재익 --
     * @since -- 1월 27일 --
     */
    @GetMapping("/{id}")
    public BookDTO searchDetailBook(@PathVariable("id") int bookId) {
        return bookService.searchDetailsBook(bookId);
    }

    /**
     * 도서 찜하기,취소하기 기능
     * 멤버id와 책id가 저장되어있는 favoriteDto값을 받아옴
     * 책 id와 멤버의 id값을 이용하여 FavoriteRepository에 접근하여 찜하거나 찜취소 구현
     *
     * @param -- FavoriteDTO --
     * @author -- 정재익 --
     * @since -- 1월 27일 --
     */
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> favoriteBook(@RequestBody FavoriteDTO favoriteDto) {
        bookService.favoriteBook(favoriteDto);
        return ResponseEntity.ok().build();
    }


    /**
     * -- 찜한 책 목록을 확인하는 메소드 --
     * 1. memberDto를 받아 memberId를 빼낸다
     * 2. favoriteRepository에서 해당 멤버가 찜한 책 목록을 반환받는다
     *
     * @param -- MemberDTO  --
     * @return -- List<BookSimpleDTO> --
     * @author -- 정재익 --
     * @since -- 1월 27일 --
     */
    @GetMapping("/favorite")
    public List<BookSimpleDTO> searchFavoriteBooks(@RequestBody MemberDTO memberDto) {
        return bookService.searchFavoriteBooks(memberDto);


    }

}
