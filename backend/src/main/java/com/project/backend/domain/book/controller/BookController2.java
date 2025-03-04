package com.project.backend.domain.book.controller;

import com.project.backend.domain.book.dto.BookDTO;
import com.project.backend.domain.book.service.BookService2;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController2 {

    private final BookService2 bookService2;

    public BookController2(BookService2 bookService2) {
        this.bookService2 = bookService2;
    }

    /**
     * 도서 찜하기,취소하기 기능
     *
     * @param -- isbn --
     * @param -- 프론트에 있는 bookdto
     * @param -- customUserDetails 로그인한 사용자 정보 --
     * @return -- GenericResponse<String>
     * @author -- 정재익, 김남우 --
     * @since -- 2월 9일 --
     */
    @PostMapping("/{isbn}/favorite")
    @Operation(summary = "도서 찜하기 / 찜취소하기")
    public ResponseEntity<GenericResponse<String>> favoriteBook(
    @PathVariable(name = "isbn") String isbn,
    @RequestBody BookDTO bookDto,
    @AuthenticationPrincipal CustomUserDetails customUserDetails) {

//        bookDto.setIsbn(isbn);
        boolean isFavorited = bookService2.favoriteBook(bookDto, customUserDetails.getUsername());

        return isFavorited
        ? ResponseEntity.status(HttpStatus.CREATED).body(GenericResponse.of("찜한 도서가 추가되었습니다."))
        : ResponseEntity.ok(GenericResponse.of("찜한 도서가 취소되었습니다."));
    }

    /**
     * -- 찜 도서 목록 확인 메소드 --
     * 로그인한 사용자의 정보를 통해 favoriteRepository에서 찜한 도서 목록 조회
     *
     * @param customUserDetails 로그인한 사용자 정보
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return GenericResponse<Page<BookDTO>>
     * @author 김남우
     * @since 2월 10일
     */
    @GetMapping("/favorite")
    @Operation(summary = "도서 찜 목록")
    public ResponseEntity<GenericResponse<Page<BookDTO>>> getFavoriteBooks(
    @AuthenticationPrincipal CustomUserDetails customUserDetails,
    @RequestParam(name = "page", defaultValue = "1") int page,
    @RequestParam(name = "size", defaultValue = "10") int size) {

        Page<BookDTO> favoriteBooks = bookService2.getFavoriteBooks(customUserDetails.getUsername(), page, size);
        return ResponseEntity.ok(GenericResponse.of(favoriteBooks, "찜한 도서 목록입니다."));
    }
}
