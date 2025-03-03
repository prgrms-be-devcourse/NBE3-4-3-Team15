package com.project.backend.domain.book.entity

import com.project.backend.domain.book.key.FavoriteId
import com.project.backend.domain.member.entity.Member
import jakarta.persistence.*

/**
 * -- 찜 엔티티 --
 *
 * @author -- 김남우 --
 * @since -- 3월 3일 --
 */
@Entity
class Favorite(

    @EmbeddedId
    val id: FavoriteId,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    val book: Book,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    val member: Member
)