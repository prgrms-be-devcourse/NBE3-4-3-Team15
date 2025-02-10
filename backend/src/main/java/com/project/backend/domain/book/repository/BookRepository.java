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

    /**
     * -- 도서의 찜 개수 업데이트 --
     * 특정 도서의 찜 개수를 지정된 값만큼 증가 또는 감소
     *
     * @param book 찜 개수를 업데이트할 도서 객체
     * @param amount 증가 또는 감소할 찜 개수 값
     * @author -- 정재익 --
     * @since -- 2월 10일 --
     */
    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.favoriteCount = b.favoriteCount + :amount WHERE b.id = :#{#book.id}")
    void updateFavoriteCount(@Param("book") Book book, @Param("amount") int amount);
}