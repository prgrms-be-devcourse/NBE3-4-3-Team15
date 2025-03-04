package com.project.backend.domain.book.controller

import com.project.backend.domain.book.dto.BookDTO
import com.project.backend.domain.book.service.BookService
import com.project.backend.global.response.GenericResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * -- 도서 컨트롤러 --
 *
 * @author -- 정재익 --
 * @since -- 2월 5일 --
 */
@Tag(name = "BookController", description = "도서 컨트롤러")
@RestController
@RequestMapping("/book")
@SecurityRequirement(name = "bearerAuth")
class BookController(private val bookService: BookService) {

    /**
     * -- 베스트셀러 반환 --
     * DB에서 yes24의 실시간 베스트셀러 정보 가져옴
     * 랭킹 1위부터 100까지 있음 1위부터 정렬되어 반환
     * DB 무결성 유지를 위해 isbn이 존재하지 않는 책은 반환하지 않음
     *
     * @param -- page 페이지 --
     * @param -- size 한 페이지에 보여주는 책 수량 --
     * @return -- ResponseEntity<GenericResponse<Page<BookDTO>>> --
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    @GetMapping
    @Operation(summary = "베스트셀러")
    fun mainPage(@RequestParam(name = "page") page: Int = 0,
                 @RequestParam(name = "size") size: Int = 10
    ): ResponseEntity<GenericResponse<Page<BookDTO>>> {
        val bestSellers = bookService.searchBestSellersDB(page, size)
        return ResponseEntity.ok(GenericResponse.of(bestSellers))
    }

    /**
     * -- 도서 통합 검색 --
     * DB의 전문검색 인덱싱을 이용하여 제목과 설명을 분석하여 검색어와 관련된 책 데이터를 300개 반환함
     * DB의 관련 데이터가 300개가 되지 않을시 네이버,카카오 API를 이용하여 책을 보충해서 반환함
     *
     * @param -- query(검색어)
     * @param -- page 페이지 --
     * @param -- size 한 페이지에 보여주는 책 수량 --
     * @return -- ResponseEntity<GenericResponse<Page<BookDTO>>> --
     * @author -- 정재익 --
     * @since -- 3월 04일 --
     */
    @GetMapping
    @Operation(summary = "도서 검색")
    fun searchBooks(
        @RequestParam(name = "query") query: String,
        @RequestParam(name = "page") page: Int = 0,
        @RequestParam(name = "size") size: Int = 10
    ): ResponseEntity<GenericResponse<Page<BookDTO>>> {
        val books = bookService.searchBooks(query, page, size)
        return ResponseEntity.ok(GenericResponse.of(books))
    }

    /**
     * -- 도서 상세 검색 --
     * book id로 DB의 정보를 가져옴
     *
     * @param -- id 책 아이디 --
     * @return -- ResponseEntity<GenericResponse<Page<BookDTO>>> --
     * @author -- 정재익 --
     * @since -- 2월 11일 --
     */
    @GetMapping("/{id}")
    @Operation(summary = "도서 상세 검색")
    fun searchDetailBooks(@PathVariable(name = "id") id: Long
    ): ResponseEntity<GenericResponse<BookDTO>> {
        val detailBook = bookService.searchDetailBooks(id)
        return ResponseEntity.ok(GenericResponse.of(detailBook))
    }

//    /**
//     * 도서 찜하기,취소하기 기능
//     *
//     * @param -- isbn --
//     * @param -- 프론트에 있는 bookdto
//     * @param -- customUserDetails 로그인한 사용자 정보 --
//     * @return -- GenericResponse<String>
//     * @author -- 정재익, 김남우 --
//     * @since -- 2월 9일 --
//     */
//    @PostMapping("/{isbn}/favorite")
//    @Operation(summary = "도서 찜하기 / 찜취소하기")
//    public ResponseEntity<GenericResponse<String>> favoriteBook(
//    @PathVariable(name = "isbn") String isbn,
//    @RequestBody BookDTO bookDto,
//    @AuthenticationPrincipal CustomUserDetails customUserDetails) {
//
//        bookDto.setIsbn(isbn);
//        boolean isFavorited = bookService.favoriteBook(bookDto, customUserDetails.getUsername());
//
//        return isFavorited
//        ? ResponseEntity.status(HttpStatus.CREATED).body(GenericResponse.of("찜한 도서가 추가되었습니다."))
//        : ResponseEntity.ok(GenericResponse.of("찜한 도서가 취소되었습니다."));
//    }
//
//    /**
//     * -- 찜 도서 목록 확인 메소드 --
//     * 로그인한 사용자의 정보를 통해 favoriteRepository에서 찜한 도서 목록 조회
//     *
//     * @param customUserDetails 로그인한 사용자 정보
//     * @param page 페이지 번호 (기본값: 0)
//     * @param size 페이지 크기 (기본값: 10)
//     * @return GenericResponse<Page<BookDTO>>
//     * @author 김남우
//     * @since 2월 10일
//     */
//    @GetMapping("/favorite")
//    @Operation(summary = "도서 찜 목록")
//    public ResponseEntity<GenericResponse<Page<BookDTO>>> getFavoriteBooks(
//    @AuthenticationPrincipal CustomUserDetails customUserDetails,
//    @RequestParam(name = "page", defaultValue = "1") int page,
//    @RequestParam(name = "size", defaultValue = "10") int size) {
//
//        Page<BookDTO> favoriteBooks = bookService.getFavoriteBooks(customUserDetails.getUsername(), page, size);
//        return ResponseEntity.ok(GenericResponse.of(favoriteBooks, "찜한 도서 목록입니다."));
//    }
}
