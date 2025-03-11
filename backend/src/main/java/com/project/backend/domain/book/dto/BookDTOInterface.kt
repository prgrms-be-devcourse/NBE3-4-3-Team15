package com.project.backend.domain.book.dto

/**
 * -- BookDTO 인터페이스 --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
interface BookDTOInterface {
    val title: String
    val author: String
    val description: String
    val image: String
    val isbn: String
}