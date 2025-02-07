package com.project.backend.domain.book.repository;

import com.project.backend.domain.book.entity.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * -- 책 저장소 --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    List<Book> findAll(Sort sort);

    @Modifying
    @Query("UPDATE Book b SET b.favoriteCount = b.favoriteCount + 1 WHERE b.isbn = :isbn")
    void incrementFavoriteCount(@Param("isbn") String isbn);

    @Modifying
    @Query("UPDATE Book b SET b.favoriteCount = b.favoriteCount - 1 WHERE b.isbn = :isbn")
    void decrementFavoriteCount(@Param("isbn") String isbn);
}