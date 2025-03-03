package com.project.backend.domain.book.repository

import com.project.backend.domain.book.entity.Book
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * -- 책 저장소 --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
@Repository
interface BookRepository : JpaRepository<Book, Long> {
    /**
     * -- 중복 isbn을 리스트로 반환 --
     *
     * @param -- isbns 새로들어오는 책 데이터의 isbn 리스트 --
     * @return -- List<String> 중복만 포함한 isbn 리스트 --
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    @Query("SELECT b.isbn FROM Book b WHERE b.isbn IN :isbns")
    fun findExistingIsbns(@Param("isbns") isbns: List<String>): List<String>

    /**
     * -- 전문검색 인덱싱을 n-gram분석 알고리즘으로 구현하여 제목과 설명에서 단어를 뽑아내어 검색어와 관련있는 책을 반환하는 메서드 --
     *
     * @return -- List<Book> 검색어 결과 --
     *
     * @author -- 정재익 --
     * @since -- 3월 03일 --
     */
    @Query(
        value = "SELECT * FROM book " +
                "WHERE MATCH(title, description) AGAINST(:keyword IN NATURAL LANGUAGE MODE)",
        nativeQuery = true
    )
    fun searchFullText(@Param("keyword") keyword: String): List<Book>

    /**
     * -- isbn을 가진 Book 반환 --
     *
     * @param -- isbn --
     * @return -- Book? --
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    fun findByIsbn(isbn: String): Book?

    /**
     * -- 베스트셀러를 1위부터 반환 --
     *
     * @return -- List<Book> 베스트셀러리스트 --
     * @author -- 정재익 --
     * @since -- 3월 1일 --
     */
    fun findByRankingIsNotNullOrderByRankingAsc(): List<Book>

    /**
     * -- DB의 베스트셀러 랭킹 초기화 --
     *
     * @author -- 정재익 --
     * @since -- 3월 1일 --
     */
    @Modifying
    @Query("UPDATE Book b SET b.ranking = NULL")
    fun resetAllRankings()

    /**
     * -- 도서의 찜 개수 업데이트 --
     * 특정 도서의 찜 개수를 지정된 값만큼 증가 또는 감소
     *
     * @param book 찜 개수를 업데이트할 도서 객체
     * @param amount 증가 또는 감소할 찜 개수 값
     * @author -- 김남우 --
     * @since -- 3월 3일 --
     */
    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.favoriteCount = b.favoriteCount + :amount WHERE b.id = :#{#book.id}")
    fun updateFavoriteCount(@Param("book") book: Book?, @Param("amount") amount: Int)

    /**
     * -- 도서 찜 개수 감소 --
     * 특정 도서의 찜 개수를 1 감소
     * 단, 찜 개수가 0 이하로 내려가지 않도록 방지
     *
     * @param bookId 찜 개수를 감소할 대상 도서의 ID
     * @author -- 김남우 --
     * @since -- 3월 3일 --
     */
    @Transactional
    @Modifying
    @Query("UPDATE Book b SET b.favoriteCount = b.favoriteCount - 1 WHERE b.id = :bookId AND b.favoriteCount > 0")
    fun decreaseFavoriteCount(@Param("bookId") bookId: Long?)

    /**
     * -- 찜 개수가 0인 도서 삭제 --
     *
     * @author -- 김남우 --
     * @since -- 3월 3일 --
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM Book b WHERE b.favoriteCount = 0")
    fun deleteBooksZeroFavoriteCount()
}