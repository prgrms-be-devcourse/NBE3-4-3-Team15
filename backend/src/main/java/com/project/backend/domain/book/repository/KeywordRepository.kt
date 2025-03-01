package com.project.backend.domain.book.repository

import com.project.backend.domain.book.entity.Keyword
import org.springframework.data.jpa.repository.JpaRepository

/**
 * -- 키워드 저장소 --
 *
 * @author -- 정재익 --
 * @since -- 3월 01일 --
 */
interface KeywordRepository : JpaRepository<Keyword, String> {
    fun existsByKeyword(keyword: String): Boolean
}
