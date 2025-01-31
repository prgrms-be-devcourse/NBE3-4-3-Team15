package com.project.backend.domain.book.controller;

import com.project.backend.domain.book.dto.BookDTO;
import com.project.backend.domain.book.dto.BookSimpleDTO;
import com.project.backend.domain.book.dto.FavoriteDTO;
import com.project.backend.domain.book.service.BookService;
import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.global.response.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * -- 도서 관련 작업을 처리하는 컨트롤러 --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
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
     * @return -- GenericResponse<List<BookSimpleDTO>> --
     * @author -- 정재익 --
     * @since -- 1월 28일 --
     */
    @GetMapping
    public GenericResponse<List<BookSimpleDTO>> searchTitleBooks(@RequestParam("title") String title) {
        return GenericResponse.of(bookService.searchTitleBooks(title), "해당 제목의 도서 목록 입니다.");
    }

    /**
     * -- 도서 조회 --
     *
     * @param -- SortBy(정렬 기준) --
     * @param -- direction(정렬 방향) --
     * @return -- GenericResponse<List<BookSimpleDTO>> --
     * DB에 있는 책을 토대로 도서를 조회함
     * 많은 책을 간략하게 보여줄때는 BooksimpleDto를 사용하고 책 세부 내용을 보여줄 때는 BookDto를 사용
     * 기본정렬은 isbn의 내림차순이고 제목,작가,설명,추천순 내림차순 오름차순 가능함
     * @author -- 정재익 --
     * @since -- 1월 31일 --
     */
    @GetMapping("/list")
    public GenericResponse<List<BookSimpleDTO>> searchAllBooks(@RequestParam(defaultValue = "id") String sortBy,
                                                               @RequestParam(defaultValue = "desc") String direction) {
        return GenericResponse.of(bookService.searchAllBooks(sortBy, direction));
    }

    /**
     * -- 도서 상세 조회 --
     * DB에 있는 책의 상세정보를 조회
     *
     * @param -- bookId(책 isbn) --
     * @return -- GenericResponse<BookDTO> --
     * @author -- 정재익 --
     * @since -- 1월 31일 --
     */
    @GetMapping("/{id}")
    public GenericResponse<BookDTO> searchDetailBook(@PathVariable("id") String bookId) {
        return GenericResponse.of(bookService.searchDetailsBook(bookId));
    }

    /**
     * 도서 찜하기,취소하기 기능
     * 멤버id와 책id가 저장되어있는 favoriteDto값을 받아옴
     * 책 id와 멤버의 id값을 이용하여 FavoriteRepository에 접근하여 찜하거나 찜취소 구현
     * 달라진 도서별 찜 개수를 BookRepository에 저장
     *
     * @param -- FavoriteDTO --
     * @return -- GenericResponse<String>
     * @author -- 정재익 --
     * @since -- 1월 31일 --
     */
    @PostMapping("/{id}/favorite")
    public GenericResponse<String> favoriteBook(@Valid @RequestBody FavoriteDTO favoriteDto) {
        return bookService.favoriteBook(favoriteDto);
    }

    /**
     * -- 찜한 책 목록을 확인하는 메소드 --
     * memberDto를 받아 memberId를 빼낸다
     * favoriteRepository에서 해당 멤버가 찜한 책 목록을 반환받는다
     * 현재 MemberDto가 유효성검사를 통과하지 못하여 @Valid 어노테이션 제거한 상태
     *
     * @param -- MemberDTO  --
     * @return -- GenericResponse<List<BookSimpleDTO>> --
     * @author -- 정재익 --
     * @since -- 1월 31일 --
     */
    @GetMapping("/favorite")
    public GenericResponse<List<BookSimpleDTO>> searchFavoriteBooks(@RequestBody MemberDto memberDto) {
        return GenericResponse.of(bookService.searchFavoriteBooks(memberDto));
    }
}