package com.project.backend.domain.book.dto

/**
 * -- Book엔티티의 DTO --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
data class BookDTO(
    val id: Long?,
    val title: String?,
    val author: String?,
    val description: String?,
    val image: String?,
    val isbn: String?,
    val ranking: Int?,
    var favoriteCount: Int? = 0)
