package com.project.backend.domain.book.repository;

import com.project.backend.domain.book.entity.Book;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * -- 책 저장소 --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    Book findByIsbn(String isbn);

    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.favoriteCount = b.favoriteCount + :amount WHERE b.id = :#{#book.id}")
    void updateFavoriteCount(@Param("isbn") String isbn, @Param("amount") int amount);

    void deleteByIsbn(String isbn);
}