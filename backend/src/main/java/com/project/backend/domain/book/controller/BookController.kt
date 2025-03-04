package com.project.backend.domain.book.controller

import com.project.backend.domain.book.dto.BookDTO
import com.project.backend.domain.book.service.BookService
import com.project.backend.global.authority.CustomUserDetails
import com.project.backend.global.response.GenericResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
     * -- 베스트셀러 검색 --
     * DB에서 yes24의 실시간 베스트셀러 정보 가져옴
     * 랭킹 1위부터 100까지 있음 1위 부터 정렬되어 반환
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
                 @RequestParam(name = "size") size: Int = 20
    ): ResponseEntity<GenericResponse<Page<BookDTO>>> {
        val bestSellers = bookService.searchBestSellersDB(page, size)
        return ResponseEntity.ok(GenericResponse.of(bestSellers))
    }

    /**
     * -- 도서 검색 --
     * api의 정보를 바탕으로 도서를 검색
     * 작가, 제목을 통합 검색
     *
     * @param -- query(검색어)
     * @param -- page 페이지 --
     * @param -- size 한 페이지에 보여주는 책 수량 --
     * @return -- ResponseEntity<GenericResponse<Page<BookDTO>>> --
     * @author -- 정재익 --
     * @since -- 2월 10일 --
     */
    @GetMapping
    @Operation(summary = "도서 검색")
    fun searchBooks(
        @RequestParam(name = "query") query: String?,
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

    /**
     * 도서 찜하기,취소하기 기능
     *
     * @param -- isbn --
     * @param -- 프론트에 있는 bookdto
     * @param -- customUserDetails 로그인한 사용자 정보 --
     * @return -- GenericResponse<String>
     * @author -- 김남우 --
     * @since -- 3월 4일 --
     */
    @PostMapping("/{isbn}/favorite")
    @Operation(summary = "도서 찜하기 / 찜취소하기")
    fun favoriteBook(
        @PathVariable(name = "isbn") isbn: String?,
        @RequestBody bookDto: BookDTO,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ): ResponseEntity<GenericResponse<String>> {
        val updatedBookDto = bookDto.copy(isbn = isbn)
        val isFavorited = bookService.favoriteBook(updatedBookDto, customUserDetails.username)

        return if (isFavorited)
            ResponseEntity.status(HttpStatus.CREATED).body(GenericResponse.of("찜한 도서가 추가되었습니다."))
        else
            ResponseEntity.ok(GenericResponse.of("찜한 도서가 취소되었습니다."))
    }
}