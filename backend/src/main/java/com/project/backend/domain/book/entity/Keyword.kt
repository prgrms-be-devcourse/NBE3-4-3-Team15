package com.project.backend.domain.book.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * -- 키워드 엔티티 --
 *
 * @author -- 정재익 --
 * @since -- 3월 01일 --
 */
@Entity
@Table(name = "keyword")
class Keyword(
    @Id
    @Column(unique = true, nullable = false)
    val keyword: String
)
