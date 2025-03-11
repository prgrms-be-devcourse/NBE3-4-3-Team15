package com.project.backend.domain.book.util

import com.project.backend.domain.book.dto.BookDTO
import com.project.backend.domain.book.entity.Book
import org.springframework.stereotype.Component

/**
 * --도서에 쓰이는 유틸관련 클래스--
 *
 * @author -- 정재익 --
 * @since -- 2월 5일 --
 */
@Component
object BookUtil {

    /**
     * -- ISBN 정제 메소드 --
     * 카카오 API에서 제공하는 ISBN은 "10자리ISBN 13자리ISBN" 형식이므로
     * 13자리 ISBN만 추출하여 반환한다.
     *
     * @param --String isbn --
     * @return String 정제된 13자리 ISBN
     * @author -- 정재익 --
     * @since -- 2월 5일 --
     */
    fun extractIsbn(isbn: String?): String {
        return isbn?.split(" ")?.getOrNull(1) ?: isbn ?: ""
    }

    /**
     * -- 중복 도서 제거 메소드 --
     * ISBN이 동일한 도서가 있을 경우 하나만 남긴다.
     *
     * @param books 중복이 포함된 도서 리스트
     * @return 중복 제거된 도서 리스트
     * @author 정재익
     * @since 2월 5일
     */
    fun removeDuplicateBooks(books: List<BookDTO>): List<BookDTO> {
        val isbns = mutableSetOf<String>()
        return books.filter { isbns.add(it.isbn) }
    }

    /**
     * -- 엔티티 DTO 변환 메소드 --
     *
     * @param -- book --
     * @return -- BookDTO --
     * @author -- 정재익 --
     * @since -- 2월 11일 --
     */
    fun entityToDTO(book: Book) = book.run {
        BookDTO(id!!, title, author, description, image, isbn, ranking, favoriteCount)
    }
}