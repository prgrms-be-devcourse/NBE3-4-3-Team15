package com.project.backend.domain.book.key

import jakarta.persistence.Embeddable
import java.io.Serializable

/**
 * -- Favorite의 복합키를 관리하는 클래스 --
 *
 * @author -- 김남우 --
 * @since -- 3월 3일 --
 */
@Embeddable
data class FavoriteId(
    val memberId: Long,
    val bookId: Long
) : Serializable