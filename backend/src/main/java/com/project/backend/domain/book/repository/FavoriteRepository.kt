package com.project.backend.domain.book.repository;

import com.project.backend.domain.book.dto.BookDTO
import com.project.backend.domain.book.entity.Favorite
import com.project.backend.domain.book.key.FavoriteId
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.List

/**
 * -- 찜 저장소 --
 *
 * @author -- 김남우 --
 * @since -- 3월 3일 --
 */
@Repository
interface FavoriteRepository : JpaRepository<Favorite, FavoriteId> {
    /**
     * -- 특정 회원이 찜한 도서 목록 조회 --
     *
     * @param memberId 회원 ID
     * @param pageable 페이징 처리 객체
     * @return 회원이 찜한 도서 목록
     * @author -- 김남우 --
     * @since -- 3월 3일 --
     */
    @Query(
        ("SELECT new com.project.backend.domain.book.dto.BookDTO(b.id, b.title, b.author, b.description, b.image, b.isbn, b.ranking, b.favoriteCount) " +
                "FROM Favorite f " +
                "JOIN f.book b " +
                "WHERE f.member.id = :memberId")
    )
    fun findFavoriteBooksByMemberId(@Param("memberId") memberId: Long, pageable: Pageable): Page<BookDTO>

    /**
     * -- 특정 회원의 찜 목록 삭제 --
     *
     * @param memberId 회원 ID
     * @author -- 김남우 --
     * @since -- 3월 3일 --
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Favorite f WHERE f.member.id = :memberId")
    fun deleteByMemberId(@Param("memberId") memberId: Long)

    /**
     * -- 특정 회원이 찜한 도서 ID 목록 조회 --
     *
     * @param memberId 회원 ID
     * @return 회원이 찜한 도서 ID 목록
     * @author -- 김남우 --
     * @since -- 3월 3일 --
     */
    @Query("SELECT f.book.id FROM Favorite f WHERE f.member.id = :memberId")
    fun findBookIdsByMemberId(@Param("memberId") memberId: Long): List<Long>

    /**
     * 특정 기간 동안 등록된 찜 수를 도서별로 집계하여 조회하는 메서드
     *
     * @param start 조회 시작 날짜
     * @param end 조회 종료 날짜
     * @return 각 도서의 ID와 해당 기간 동안 추가된 즐겨찾기 개수를 포함한 리스트
     *
     * @author 김남우
     * @since 2025.03.06
     */
    @Query("SELECT f.book.id, COUNT(f.id) FROM Favorite f WHERE f.favoritedAt BETWEEN :start AND :end GROUP BY f.book.id")
    fun findFavoriteCounts(
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime
    ): kotlin.collections.List<Array<Any>>
}