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

    /**
     * -- 도서 찜 개수 감소 --
     * 특정 도서의 찜 개수를 1 감소
     * 단, 찜 개수가 0 이하로 내려가지 않도록 방지
     *
     * @param bookId 찜 개수를 감소할 대상 도서의 ID
     * @author -- 김남우 --
     * @since -- 2월 10일 --
     */
    @Transactional
    @Modifying
    @Query("UPDATE Book b SET b.favoriteCount = b.favoriteCount - 1 WHERE b.id = :bookId AND b.favoriteCount > 0")
    void decreaseFavoriteCount(@Param("bookId") Long bookId);

    /**
     * -- 찜 개수가 0인 도서 삭제 --
     *
     * @author -- 김남우 --
     * @since -- 2월 10일 --
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM Book b WHERE b.favoriteCount = 0")
    void deleteBooksZeroFavoriteCount();
}