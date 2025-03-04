package com.project.backend.domain.book.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.backend.domain.book.dto.BookDTO
import com.project.backend.domain.book.dto.KakaoDTO
import com.project.backend.domain.book.dto.NaverDTO
import com.project.backend.domain.book.entity.Book
import com.project.backend.domain.book.entity.Favorite
import com.project.backend.domain.book.entity.Keyword
import com.project.backend.domain.book.exception.BookErrorCode
import com.project.backend.domain.book.exception.BookException
import com.project.backend.domain.book.key.FavoriteId
import com.project.backend.domain.book.repository.BookRepository
import com.project.backend.domain.book.repository.FavoriteRepository
import com.project.backend.domain.book.repository.KeywordRepository
import com.project.backend.domain.book.util.BookUtil
import com.project.backend.domain.member.exception.MemberErrorCode
import com.project.backend.domain.member.exception.MemberException
import com.project.backend.domain.member.repository.MemberRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.Page
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


/**
 * -- 도서 서비스 클래스 --
 *
 * @author -- 정재익 --
 * @since -- 2월 5일 --
 */
@Service
class BookService(
    private val bookRepository: BookRepository,
    private val objectMapper: ObjectMapper,
    private val keywordRepository: KeywordRepository,
    private val memberRepository: MemberRepository,
    private val favoriteRepository: FavoriteRepository,
    @Value("\${naver.client-id}") val clientId: String,
    @Value("\${naver.client-secret}") val clientSecret: String,
    @Value("\${naver.book-search-url}") val naverUrl: String,
    @Value("\${kakao.key}") val kakaoKey: String,
    @Value("\${kakao.url}") val kakaoUrl: String
) {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * -- 도서 검색 메소드 --
     * 1. DB에 도서 검색
     * 2. 관련 도서가 200건이 안될시 API 요청
     * 3. 처음엔 소량을 요청하고 그래도 200권이 안되면 수량을 늘려 요청
     * 4. 3회까지 요청하고도 200권이 안되면 요청 종료
     * 5. Page<BookDto>로 변환하여 반환
     *
     * @param -- query(검색어)
     * @param -- page 페이지 --
     * @param -- size 한 페이지에 보여주는 책 수량 --
     * @return -- Page<BookDTO> --
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    @Transactional
    fun searchBooks(query: String?, page: Int, size: Int): Page<BookDTO> {
        if (query.isNullOrBlank()) {
            throw BookException(BookErrorCode.QUERY_EMPTY)
        }

        var bookList = searchBooksDB(query)

        if (keywordRepository.existsByKeyword(query)) {
            return BookUtil.pagingBooks(page, size, bookList)
        }

        keywordRepository.save(Keyword(query))


        var start = 0
        val end = 3
        while (bookList.size < 200 && start < end) {

            val apiBooks = mutableListOf<BookDTO>()

            when (start) {
                0 -> {
                    apiBooks += requestApi(query, "naver", 1, 0)
                    apiBooks += requestApi(query, "kakao", 0, 2)
                }

                1 -> {
                    apiBooks += requestApi(query, "naver", 100, 0)
                    apiBooks += requestApi(query, "kakao", 0, 4)
                }

                2 -> {
                    apiBooks += requestApi(query, "kakao", 0, 10)
                }
            }
            saveBooks(apiBooks)
            bookList = searchBooksDB(query)

            if (bookList.size >= 200) {
                break
            }
            start++
        }
        return BookUtil.pagingBooks(page, size, bookList)
    }

    /**
     * -- Api 요청 메소드 --
     * 네이버 도서와 카카오 도서 Api 요청을 통합한 메서드
     *
     * @param -- query 검색어 --
     * @param -- apiType 요청하는 Api 종류 --
     * @param -- naverStart 네이버 api 검색 시작 위치 --
     * @param -- kakaoPage 카카오 api 검색 페이지 수 --
     *
     * @return -- List<BookDTO> --
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    private fun requestApi(query: String, apiType: String, naverStart: Int, kakaoPage: Int): List<BookDTO> {
        val restTemplate = RestTemplate(SimpleClientHttpRequestFactory())
        val (headers, url, responseKey) = getApiRequestParams(query, apiType, naverStart, kakaoPage)

        val entity = HttpEntity<String>(headers)
        val response: ResponseEntity<Map<String, Any>> = restTemplate.exchange(
            url, HttpMethod.GET, entity, object : ParameterizedTypeReference<Map<String, Any>>() {}
        )

        val rawData = (response.body?.get(responseKey) as? List<*>)?.filterNotNull()
            ?: throw BookException(BookErrorCode.BOOK_NOT_FOUND)

        return rawData.map { convertToBook(it, apiType) }
    }

    /**
     * -- Api 설정 메소드 --
     * 네이버 도서와 카카오 도서 종류에 따라 다른 http헤더와 주소값을 반환한다
     *
     * @param -- query 검색어 --
     * @param -- apiType 요청하는 Api 종류 --
     * @param -- naverStart 네이버 api 검색 시작 위치 --
     * @param -- kakaoPage 카카오 api 검색 페이지 수 --
     *
     * @return -- Triple<HttpHeaders, String, String> --
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    private fun getApiRequestParams(
        query: String,
        apiType: String,
        naverStart: Int,
        kakaoPage: Int
    ): Triple<HttpHeaders, String, String> {
        val headers = HttpHeaders()

        return when (apiType.lowercase()) {
            "kakao" -> {
                headers["Authorization"] = "KakaoAK $kakaoKey"
                Triple(headers, "$kakaoUrl?query=$query&target=author&page=$kakaoPage&size=50", "documents")
            }

            else -> {
                headers["X-Naver-Client-Id"] = clientId
                headers["X-Naver-Client-Secret"] = clientSecret
                Triple(headers, "$naverUrl?query=$query&display=100&start=$naverStart", "items")
            }
        }
    }

    /**
     * -- 통합 검색 메소드 --
     * 검색어를 기반으로 제목과 설명을 조사하여 관련된 책을 최대 200개까지 반환하는 메소드
     *
     * @param -- query (검색어) --
     * @return -- List<BookDTO> --
     * @author -- 정재익 --
     * @since -- 3월 03일 --
     */
    fun searchBooksDB(query: String): List<BookDTO> {
        return bookRepository.searchFullText(query)
            .take(200)
            .map { BookUtil.entityToDTO(it) }
    }

    /**
     * -- 도서 상세 검색 메소드 --
     *
     * @param -- id 책 아이디--
     * @return -- BookDTO --
     * @author -- 정재익 --
     * @since -- 2월 11일 --
     */
    fun searchDetailBooks(id: Long): BookDTO {
        return bookRepository.findById(id)
            .map { BookUtil.entityToDTO(it) }
            .orElseThrow { BookException(BookErrorCode.BOOK_NOT_FOUND) }
    }

    /**
     * -- 베스트셀러 검색 메소드 --
     *
     * @param -- page 페이지--
     * @param -- size 페이지에 보이는 수량--
     * @return -- Page<BookDTO> --
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    fun searchBestSellersDB(page: Int, size: Int): Page<BookDTO> {
        val bestSellers = bookRepository.findByRankingIsNotNullOrderByRankingAsc()
            .map { BookUtil.entityToDTO(it) }
        return BookUtil.pagingBooks(page, size, bestSellers)
    }

    /**
     * -- db 저장 메소드 --
     * isbn을 통해 중복검사
     *
     * @param -- List<Book> 중복이 제거되지 않은 책 목록 --
     * @author -- 정재익 --
     * @since -- 2월 11일 --
     */
    @Transactional
    fun saveBooks(books: List<BookDTO>) {
        val uniqueBooks = BookUtil.removeDuplicateBooks(books)
        val existingIsbns = bookRepository.findExistingIsbns(uniqueBooks.mapNotNull { it.isbn }).toSet()


        val booksToSave = uniqueBooks.filterNot { existingIsbns.contains(it.isbn) }.map { dto ->
            Book(
                id = null,
                title = dto.title ?: "제목 정보가 없습니다",
                author = dto.author ?: "작가 정보가 없습니다",
                description = dto.description ?: "설명 정보가 없습니다",
                image = dto.image ?: "이미지 파일이 없습니다",
                isbn = dto.isbn ?: "isbn 정보가 없습니다",
                ranking = null,
                favoriteCount = dto.favoriteCount
            )
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
     * 존재하지 않는 책들은 새로 저장됨
     *
     * @param -- List<Book> 중복이 제거되지 않은 베스트셀러 목록 --
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    @Transactional
    fun saveBestsellers(books: List<BookDTO>) {
        bookRepository.resetAllRankings()
        books.forEach { dto ->
            val existingBook = bookRepository.findByIsbn(dto.isbn ?: "")

            if (existingBook != null) {
                existingBook.ranking = dto.ranking
                entityManager.merge(existingBook)
            } else {
                val book = Book(
                    id = null,
                    title = dto.title ?: "제목 정보가 없습니다",
                    author = dto.author ?: "작가 정보가 없습니다",
                    description = dto.description ?: "설명 정보가 없습니다",
                    image = dto.image ?: "이미지 파일이 없습니다",
                    isbn = dto.isbn ?: "ISBN 정보가 없습니다",
                    ranking = dto.ranking,
                    favoriteCount = 0
                )
                entityManager.persist(book)
            }
        }
        entityManager.flush()
        entityManager.clear()
    }

    /**
     * -- BookDTO 변환 메소드 --
     * api 응답 데이터를 BookDTO로 변환한다
     *
     * @param -- item api 응답 데이터 --
     * @param -- String apiType 네이버와 카카오 구분 --
     * @return BookDTO
     * @author 정재익
     * @since 2월 18일
     */
    private fun convertToBook(item: Any, apiType: String): BookDTO {
        val bookDto = when (apiType.lowercase()) {
            "kakao" -> objectMapper.convertValue(item, KakaoDTO::class.java)
            else -> objectMapper.convertValue(item, NaverDTO::class.java)
        }
        return BookDTO(
            id = 0L,
            title = bookDto.title,
            author = bookDto.author,
            description = bookDto.description,
            image = bookDto.image,
            isbn = bookDto.isbn,
            ranking = null,
            favoriteCount = 0
        )
    }

    /**
     * -- 도서 찜, 찜취소 메소드 --
     *
     * 책을 찜하는 기능 이미 찜을 했을 경우 찜 취소
     * 책이 받은 찜한 수를 Book DB에 최신화
     * 유저 정보와 책 id을 favorite DB에 생성 혹은 삭제
     * 책의 찜 수가 0이 될 시에 Book DB에서 책 데이터 삭제
     * 책의 정보가 책 DB에 이미 존재 할 시 같은 책을 추가하지 않고 favoritecount만 수정하여 중복 책 등록 방지
     *
     * @param -- bookDto -- 프론트에서 BODY로 받은 DTO
     * @param -- username --
     * @return -- boolean --
     * @author -- 김남우 --
     * @since -- 3월 4일 --
     */
    @Transactional
    fun favoriteBook(bookDto: BookDTO, username: String): Boolean {

        val member = memberRepository.findByUsername(username)
            .orElseThrow { MemberException(MemberErrorCode.NON_EXISTING_USERNAME) }

        val isbn = bookDto.isbn
            ?: throw BookException(BookErrorCode.ISBN_NOT_NULL)

        val book = bookRepository.findByIsbn(isbn)
            ?: throw BookException(BookErrorCode.BOOK_NOT_FOUND)

        val bookId = book.id
            ?: throw BookException(BookErrorCode.ID_NOT_NULL)

        val favoriteId = FavoriteId(member.id, bookId)

        return if (favoriteRepository.existsById(favoriteId)) {
            favoriteRepository.deleteById(favoriteId)

            if (book.favoriteCount == 1) {
                bookRepository.delete(book)
            }
            else {
                bookRepository.updateFavoriteCount(book, -1)
            }

            false
        }
        else {
            bookRepository.updateFavoriteCount(book, 1)

            val favorite = Favorite(
                id = favoriteId,
                book = book,
                member = member
            )
            favoriteRepository.save(favorite)

            true
        }
    }
}