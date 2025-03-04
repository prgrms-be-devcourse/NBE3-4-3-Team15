package com.project.backend.domain.book.service

import com.project.backend.domain.book.dto.BookDTO
import com.project.backend.domain.book.entity.Book
import com.project.backend.domain.book.exception.BookErrorCode
import com.project.backend.domain.book.exception.BookException
import com.project.backend.domain.book.repository.BookRepository
import com.project.backend.domain.book.repository.RedisRepository
import com.project.backend.domain.book.util.BookUtil
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

/**
 * -- 도서 서비스 클래스 --
 *
 * @author -- 정재익 --
 * @since -- 2월 5일 --
 */
@Service
class BookService(
    private val bookRepository: BookRepository,
    private val redisRepository: RedisRepository,
    private val apiClientService: ApiClientService,
) {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * -- 도서 검색 메소드 --
     * 1. 입력된 적이 있는 검색어 인지 Redis 이용 판단
     * 2. 입력된 적이 있으면 DB 기반 통합 검색만 시행
     * 3. 입력된 적이 없을 경우 DB 기반 통합 검색 시행하고 데이터가 300건보다 적을경우만 API 요청
     * 4. 처음엔 소량을 요청하고 그래도 300권이 안되면 수량을 늘려 요청
     * 5. 3회까지 요청하고도 300권이 안되면 요청 종료
     * 6. Page<BookDto>로 변환하여 반환
     *
     * @param -- query(검색어)
     * @param -- page 페이지 --
     * @param -- size 한 페이지에 보여주는 책 수량 --
     * @return -- Page<BookDTO> --
     *
     * @author -- 정재익 --
     * @since -- 3월 03일 --
     */
    @Transactional
    fun searchBooks(query: String, page: Int, size: Int): Page<BookDTO> {
        if (query.isBlank()) {
            throw BookException(BookErrorCode.QUERY_EMPTY)
        }

        var bookList = searchBooksDB(query, page, size)

        if (redisRepository.existKeyword(query)) {
            return searchBooksDB(query, page, size)
        }

        redisRepository.saveKeyword(query)

        var start = 0
        val end = 3
        while (bookList.size < 300 && start < end) {

            val apiBooks = mutableListOf<BookDTO>()

            when (start) {
                0 -> {
                    apiBooks += apiClientService.requestApi(query, "naver", 1, 0)
                    apiBooks += apiClientService.requestApi(query, "kakao", 0, 2)
                }

                1 -> {
                    apiBooks += apiClientService.requestApi(query, "naver", 100, 0)
                    apiBooks += apiClientService.requestApi(query, "kakao", 0, 4)
                }

                2 -> {
                    apiBooks += apiClientService.requestApi(query, "kakao", 0, 10)
                }
            }
            saveBooks(apiBooks)
            bookList = searchBooksDB(query, page, size)

            if (bookList.size >= 300) {
                break
            }
            start++
        }
        return searchBooksDB(query, page, size)
    }

    /**
     * -- 도서 통합 검색 메소드--
     * 검색어를 기반으로 제목과 설명을 조사하여 관련된 책을 반환하는 메소드
     *
     * @param -- query (검색어) --
     * @param -- page 페이지--
     * @param -- size 페이지에 보이는 수량--
     * @return -- List<BookDTO> --
     *
     * @author -- 정재익 --
     * @since -- 3월 04일 --
     */
    fun searchBooksDB(query: String, page: Int, size: Int): Page<BookDTO> {
        val pageable: Pageable = PageRequest.of(page, size)
        return bookRepository.searchFullText(query, pageable).map { BookUtil.entityToDTO(it) }
    }

    /**
     * -- 도서 상세 검색 메소드 --
     *
     * @param -- id 책 아이디--
     * @return -- BookDTO --
     *
     * @author -- 정재익 --
     * @since -- 2월 11일 --
     */
    fun searchDetailBooks(id: Long): BookDTO {
        return bookRepository.findById(id)
            .map { BookUtil.entityToDTO(it) }
            .orElseThrow { BookException(BookErrorCode.BOOK_NOT_FOUND) }
    }

    /**
     * -- 베스트셀러 반환 메소드 --
     *
     * @param -- page 페이지--
     * @param -- size 페이지에 보이는 수량--
     * @return -- Page<BookDTO> --
     *
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    fun searchBestSellersDB(page: Int, size: Int): Page<BookDTO> {
        val pageable: Pageable = PageRequest.of(page, size)
        val bestSellersPage = bookRepository.findByRankingIsNotNullOrderByRankingAsc(pageable)
        return bestSellersPage.map { BookUtil.entityToDTO(it) }
    }

    /**
     * -- db 저장 메소드 --
     * isbn을 통해 중복검사
     * 대량의 데이터가 들어올시에는 1000개씩 쪼개서 받아들임
     * DB 무결성 유지를 위해 isbn이 존재하지 않는 책들은 db에 저장하지 않음
     *
     * @param -- List<Book> 중복이 제거되지 않은 책 목록 --
     * @author -- 정재익 --
     * @since -- 3월 04일 --
     */
    @Transactional
    fun saveBooks(books: List<BookDTO>) {
        val uniqueBooks = BookUtil.removeDuplicateBooks(books).filter { it.isbn.isNotBlank() }
        val existingIsbns = bookRepository.findExistingIsbns(uniqueBooks.map { it.isbn }).toSet()

        val booksToSave = uniqueBooks.filterNot { existingIsbns.contains(it.isbn) }.map { dto ->
            Book(null, dto.title, dto.author, dto.description, dto.image, dto.isbn, null, dto.favoriteCount)
        }
        if (booksToSave.isNotEmpty()) {
            booksToSave.chunked(1000).forEach { batch ->
                batch.forEach { entityManager.persist(it) }
                entityManager.flush()
                entityManager.clear()
            }
        }
    }

    /**
     * -- 베스트셀러 db 저장 메소드 --
     * isbn을 통해 중복검사 이미 존재하는 책들은 순위만 수정되고 나머지는 이어받음
     * DB 무결성 유지를 위해 isbn이 존재하지 않는 책들은 db에 저장하지 않음
     * 존재하지 않는 책들은 새로 저장됨
     *
     * @param -- List<Book> 중복이 제거되지 않은 베스트셀러 목록 --
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    @Transactional
    fun saveBestsellers(books: List<BookDTO>) {
        bookRepository.resetAllRankings()

        books.filter { it.isbn.isNotBlank() }
            .forEach { dto ->
                val existingBook = bookRepository.findByIsbn(dto.isbn)

                if (existingBook != null) {
                    existingBook.ranking = dto.ranking
                    entityManager.merge(existingBook)
                } else {
                    val book = Book(null, dto.title, dto.author, dto.description, dto.image, dto.isbn, dto.ranking, 0)
                    entityManager.persist(book)
                }
            }
        entityManager.flush()
        entityManager.clear()
    }
}