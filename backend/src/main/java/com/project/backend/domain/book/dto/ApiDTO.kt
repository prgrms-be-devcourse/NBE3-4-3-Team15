package com.project.backend.domain.book.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.project.backend.domain.book.util.BookUtil

/**
 * -- 네이버 데이터를 받는 DTO --
 *
 * @author -- 정재익 --
 * @since -- 2월 7일 --
 */
data class NaverDTO @JsonCreator constructor(
    @param:JsonProperty("title") override val title: String,
    @param:JsonProperty("author") override val author: String,
    @param:JsonProperty("description") override val description: String,
    @param:JsonProperty("image") override val image: String,
    @param:JsonProperty("isbn") override val isbn: String
) : BookDTOInterface

/**
 * -- 카카오 데이터를 받는 DTO --
 *
 * @author -- 정재익 --
 * @since -- 2월 7일 --
 */
data class KakaoDTO @JsonCreator constructor(
    @param:JsonProperty("title") override val title: String,
    @param:JsonProperty("authors") val authors: List<String>,
    @param:JsonProperty("contents") override val description: String,
    @param:JsonProperty("thumbnail") override val image: String,
    @param:JsonProperty("isbn") private val rawIsbn: String?
) : BookDTOInterface {
    override val author: String by lazy { authors.joinToString(", ") }
    override val isbn: String by lazy { BookUtil.extractIsbn(rawIsbn) }
}