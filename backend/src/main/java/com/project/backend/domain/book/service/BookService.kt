package com.project.backend.domain.book.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.backend.domain.book.dto.BookDTO
import com.project.backend.domain.book.dto.KakaoDTO
import com.project.backend.domain.book.dto.NaverDTO
import com.project.backend.domain.book.entity.Book
import com.project.backend.domain.book.exception.BookErrorCode
import com.project.backend.domain.book.exception.BookException
import com.project.backend.domain.book.repository.BookRepository
import com.project.backend.domain.book.util.BookUtil
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
    @Value("\${naver.client-id}") val clientId: String,
    @Value("\${naver.client-secret}") val clientSecret: String,
    @Value("\${naver.book-search-url}") val naverUrl: String,
    @Value("\${kakao.key}") val kakaoKey: String,
    @Value("\${kakao.url}") val kakaoUrl: String
) {

    /**
     * -- 도서 검색 메소드 --
     * 1. 카카오와 네이버 두 Api에 요청
     * 2. Page<BookDto>로 변환하여 반환
     *
     * @param -- query(검색어)
     * @param -- page 페이지 --
     * @param -- size 한 페이지에 보여주는 책 수량 --
     * @return -- Page<BookDTO> --
     * @author -- 정재익 --
     * @since -- 2월 18일 --
     */
    fun searchBooks(query: String?, page: Int, size: Int): Page<BookDTO> {
        if (query.isNullOrBlank()) {
            throw BookException(BookErrorCode.QUERY_EMPTY)
        }

        val books = mutableListOf<BookDTO>()
        books += requestApi(query, "naver", 1)
        books += requestApi(query, "naver", 100)
        books += requestApi(query, "kakao", 0)

        saveBooks(books)
        val bookList = searchBooksDB(query)
        return BookUtil.pagingBooks(page, size, bookList)
    }

    /**
     * -- Api 요청 메소드 --
     * 네이버 도서와 카카오 도서 Api 요청을 통합한 메서드
     * 요청 받은 검색 범위와 api 종류에 따라 다른 데이터를 반환한다.
     *
     * @param -- query 검색어 --
     * @param -- apiType 요청하는 Api 종류 --
     * @param -- 네이버 api 검색 시작 위치 --
     * @return -- List<BookDTO> --
     * @author -- 정재익 --
     * @since -- 2월 10일 --
     */
    private fun requestApi(query: String, apiType: String, naverStart: Int): List<BookDTO> {
        val restTemplate = RestTemplate(SimpleClientHttpRequestFactory())
        val (headers, url, responseKey) = getApiRequestParams(query, apiType, naverStart)

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
     * @param -- 네이버 api 검색 시작 위치 --
     * @return -- Triple<HttpHeaders, String, String> --
     * @author -- 정재익 --
     * @since -- 2월 18일 --
     */
    private fun getApiRequestParams(query: String, apiType: String, naverStart: Int): Triple<HttpHeaders, String, String> {
        val headers = HttpHeaders()

        return when (apiType.lowercase()) {
            "kakao" -> {
                headers["Authorization"] = "KakaoAK $kakaoKey"
                Triple(headers, "$kakaoUrl?query=$query&target=author&page=4&size=50", "documents")
            } else -> {
                headers["X-Naver-Client-Id"] = clientId
                headers["X-Naver-Client-Secret"] = clientSecret
                Triple(headers, "$naverUrl?query=$query&display=100&start=$naverStart", "items")
            }
        }
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
            favoriteCount = 0
        )
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
     * -- db 저장 메소드 --
     * isbn을 통해 중복검사
     *
     * @param -- List<Book> 중복이 제거되지 않은 책 목록 --
     * @author -- 정재익 --
     * @since -- 2월 11일 --
     */
    fun saveBooks(books: List<BookDTO>) {
        val uniqueBooks = BookUtil.removeDuplicateBooks(books)
        val existingIsbn = bookRepository.findByIsbnIn(uniqueBooks.map { it.isbn }).map { it!!.isbn }.toSet()
        val booksToSave = uniqueBooks.filter { it.isbn !in existingIsbn } .map { dto ->
            Book(
                id = null,
                title = dto.title ?: "제목 정보가 없습니다",
                author = dto.author ?: "작가 정보가 없습니다",
                description = dto.description ?: "설명 정보가 없습니다",
                image = dto.image ?: "",
                isbn = dto.isbn,
                favoriteCount = dto.favoriteCount
            )
        }
        if (booksToSave.isNotEmpty()) {
            bookRepository.saveAll(booksToSave)
        }
    }

    /**
     * -- DB에서 관련 검색 데이터를 찾는 메소드 --
     * 책과 작가에 검색어가 포함되는 데이터를 최대 400개 까지 가져오는 메소드
     *
     * @param -- query (검색어) --
     * @return -- List<BookDTO> --
     * @author -- 정재익 --
     * @since -- 2월 11일 --
     */
    fun searchBooksDB(query: String): List<BookDTO> {
        return bookRepository.findByTitleOrAuthor(query)
            .take(400)
            .map { BookUtil.entityToDTO(it) }
    }
//
//    /**
//     * -- 도서 찜, 찜취소 메소드 --
//     *
//     * 책을 찜하는 기능 이미 찜을 했을 경우 찜 취소
//     * 책이 받은 찜한 수를 Book DB에 최신화
//     * 유저 정보와 책 id을 favorite DB에 생성 혹은 삭제
//     * 책의 찜 수가 0이 될 시에 Book DB에서 책 데이터 삭제
//     * 책의 정보가 책 DB에 이미 존재 할 시 같은 책을 추가하지 않고 favoritecount만 수정하여 중복 책 등록 방지
//     *
//     * @param -- bookDto -- 프론트에서 BODY로 받은 DTO
//     * @param -- username --
//     * @return -- boolean --
//     * @author -- 정재익, 김남우 --
//     * @since -- 2월 9일 --
//     */
//    @Transactional
//    public boolean favoriteBook(BookDTO bookDto, String username) {
//
//        Member member = memberRepository.findByUsername(username)
//            .orElseThrow(() -> new MemberException(MemberErrorCode.NON_EXISTING_USERNAME));
//
//        Book book = bookRepository.findByIsbn(bookDto.getIsbn());
//        FavoriteId favoriteId = new FavoriteId(member.getId(), book.getId());
//
//        if (favoriteRepository.existsById(favoriteId)) {
//            favoriteRepository.deleteById(favoriteId); // 먼저 favorite 테이블에서 삭제
//
//            int favoriteCount = book.getFavoriteCount();
//            if (favoriteCount == 1) {
//                bookRepository.delete(book); // favoriteCount가 1이면 Book 테이블에서 도서 삭제
//            }
//            else {
//                bookRepository.updateFavoriteCount(book, -1); // 아니면 favoriteCount 감소
//            }
//
//            return false;
//        }
//
//        else {
//            bookRepository.updateFavoriteCount(book, +1); // favoriteCount 1 증가
//
//            Favorite favorite = Favorite.builder()
//                .id(favoriteId)
//                .book(book)
//                .member(member)
//                .build();
//
//            favoriteRepository.save(favorite); // favorite 테이블에 저장
//
//            return true;
//        }
//    }
//
//    /**
//     * -- 찜 도서 목록 메소드 --
//     * 로그인한 유저의 찜 도서 목록 반환
//     *
//     * @param -- username --
//     * @return -- List<BookDTO> --
//     * @author -- 김남우 --
//     * @since -- 2월 10일 --
//     */
//    public Page<BookDTO> getFavoriteBooks(String username, int page, int size) {
//
//        Member member = memberRepository.findByUsername(username)
//            .orElseThrow(() -> new MemberException(MemberErrorCode.NON_EXISTING_USERNAME));
//
//        Pageable pageable = PageRequest.of(page - 1, size);
//
//        Page<BookDTO> favoriteBooks = favoriteRepository.findFavoriteBooksByMemberId(member.getId(), pageable); // 멤버 ID에 해당하는 찜 도서 목록 조회
//
//        if (favoriteBooks.isEmpty()) {
//            throw new BookException(BookErrorCode.NO_FAVORITE_BOOKS);
//        }
//
//        return favoriteBooks;
//    }
}