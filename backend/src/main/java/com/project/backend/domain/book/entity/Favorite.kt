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
    val member: Member,

    /**
     * 찜하기 기능에서 BaseEntity의 modifiedAt은 필요하지 않으므로,
     * 생성 시간을 직접 설정하여 관리
     */
    @Column(name = "favorited_at")
    val favoritedAt: LocalDateTime = LocalDateTime.now()
)