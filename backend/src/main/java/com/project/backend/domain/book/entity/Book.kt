package com.project.backend.domain.book.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*

/**
 * -- 책 엔티티 --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
@Entity
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(columnDefinition = "TEXT")
    @JsonProperty("title")
    val title: String,

    @Column(length = 100)
    @JsonProperty("author")
    val author: String,

    @Column(columnDefinition = "TEXT")
    @JsonProperty("description")
    val description: String,

    @Column(columnDefinition = "TEXT")
    @JsonProperty("image")
    val image: String,

    @Column(length = 50, unique = true, nullable = false)
    @JsonProperty("isbn")
    val isbn: String,

    val favoriteCount: Int
)
