package com.project.backend.domain.book.repository;

import com.project.backend.domain.book.dto.BookDTO;
import com.project.backend.domain.book.entity.Favorite;
import com.project.backend.domain.book.key.FavoriteId;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * -- 찜 저장소 --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    /**
     * -- 특정 회원이 찜한 도서 목록 조회 --
     *
     * @param memberId 회원 ID
     * @param pageable 페이징 처리 객체
     * @return 회원이 찜한 도서 목록
     * @author -- 김남우 --
     * @since -- 2월 10일 --
     */
    @Query("SELECT new com.project.backend.domain.book.dto.BookDTO(b.id, b.title, b.author, b.description, b.image, b.isbn, b.ranking, b.favoriteCount) " +
            "FROM Favorite f " +
            "JOIN f.book b " +
            "WHERE f.member.id = :memberId")
    Page<BookDTO> findFavoriteBooksByMemberId(@Param("memberId") Long memberId, Pageable pageable);
