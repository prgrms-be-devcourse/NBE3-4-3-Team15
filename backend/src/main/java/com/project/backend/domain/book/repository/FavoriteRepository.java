package com.project.backend.domain.book.repository;

import com.project.backend.domain.book.dto.BookDTO;
import com.project.backend.domain.book.entity.Favorite;
import com.project.backend.domain.book.key.FavoriteId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * -- 찜 저장소 --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    @Query("SELECT new com.project.backend.domain.book.dto.BookDTO(b.title, b.author, b.description, b.image, b.isbn, b.favoriteCount) " +
            "FROM Favorite f " +
            "JOIN f.book b " +
            "WHERE f.member.id = :memberId")
    Page<BookDTO> findFavoriteBooksByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}