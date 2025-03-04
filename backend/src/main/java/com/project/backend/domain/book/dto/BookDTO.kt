package com.project.backend.domain.book.dto

/**
 * -- Book엔티티의 DTO --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
data class BookDTO(
    val id: Long?,
    val title: String = "제목 정보 없음",
    val author: String = "작가 정보 없음",
    val description: String = "설명 정보 없음",
    val image: String = "이미지 정보 없음",
    val isbn: String,
    val ranking: Int?,
    var favoriteCount: Int? = 0)
