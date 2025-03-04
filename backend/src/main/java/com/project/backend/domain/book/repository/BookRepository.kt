package com.project.backend.domain.book.repository

import com.project.backend.domain.book.entity.Book
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
     * @param -- ketword 검색어 --
     * @return -- List<Book> 검색어 결과와 관련된 책 리스트  --
     *
     * @author -- 정재익 --
     * @since -- 3월 04일 --
     */
    @Query(
    value = """
        SELECT * FROM book 
        WHERE MATCH(title, description) AGAINST(:keyword IN NATURAL LANGUAGE MODE)
        ORDER BY MATCH(title, description) AGAINST(:keyword IN NATURAL LANGUAGE MODE) DESC
    """,
    countQuery = """
        SELECT COUNT(*) FROM book 
        WHERE MATCH(title, description) AGAINST(:keyword IN NATURAL LANGUAGE MODE)
    """,
    nativeQuery = true
    )
    fun searchFullText(
        @Param("keyword") keyword: String,
        pageable: Pageable
    ): Page<Book>

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
     * @param -- pageable --
     * @return -- List<Book> 베스트셀러 리스트 --
     *
     * @author -- 정재익 --
     * @since -- 3월 04일 --
     */
    fun findByRankingIsNotNullOrderByRankingAsc(pageable: Pageable): Page<Book>

    /**
     * -- DB의 베스트셀러 랭킹 초기화 --
     *
     * @author -- 정재익 --
     * @since -- 3월 1일 --
     */
    @Modifying
    @Query("UPDATE Book b SET b.ranking = NULL")
    fun resetAllRankings()


}