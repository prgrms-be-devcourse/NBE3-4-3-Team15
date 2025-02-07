package com.project.backend.domain.book.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.backend.domain.book.dto.BookDTO;
import com.project.backend.domain.book.dto.KakaoDTO;
import com.project.backend.domain.book.dto.NaverDTO;
import com.project.backend.domain.book.exception.BookErrorCode;
import com.project.backend.domain.book.exception.BookException;
import com.project.backend.domain.book.repository.BookRepository;
import com.project.backend.domain.book.repository.FavoriteRepository;
import com.project.backend.domain.book.util.BookUtil;
import com.project.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * -- 도서 서비스 클래스 --
 *
 * @author -- 정재익 --
 * @since -- 2월 5일 --
 */
@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private final ConcurrentHashMap<String, List<BookDTO>> bookCache = new ConcurrentHashMap<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.book-search-url}")
    private String naverUrl;

    @Value("${kakao.key}")
    private String kakaoKey;

    @Value("${kakao.url}")
    private String kakaoUrl;

    /**
     * -- 도서 검색 메소드 --
     * 1. 카카오와 네이버 두 Api에 요청
     * 2. List<BookDto>로 변환하여 반환
     *
     * @param -- title (검색어) --
     * @param -- isAuthorSearch (작가검색, 도서검색 판단) --
     * @param -- sessionId (개인별 세션 ID)
     * @return -- List<BookDTO> --
     * @author -- 정재익 --
     * @since -- 2월 5일 --
     */
    public List<BookDTO> searchBooks(String query, boolean isAuthorSearch, String sessionId) {

        if (!StringUtils.hasText(query)) {
            throw new BookException(BookErrorCode.QUERY_EMPTY);
        }

        List<BookDTO> allBooks = new ArrayList<>();

        allBooks.addAll(requestApi(query, isAuthorSearch ? "d_auth" : "d_titl", "naver"));
        allBooks.addAll(requestApi(query, isAuthorSearch ? "person" : "title", "kakao"));


        List<BookDTO> uniqueBooks = removeDuplicateBooks(allBooks);

        bookCache.put(sessionId, uniqueBooks);

        return uniqueBooks;
    }

    /**
     * -- Api 요청 메소드 --
     * 네이버 도서와 카카오 도서 Api 요청을 통합한 메서드
     * 요청 받은 검색 범위와 api 종류에 따라 다른 데이터를 반환한다.
     *
     * @param -- query 검색어 --
     * @param -- target 검색 범위 --
     * @param -- apiType 요청하는 Api 종류 --
     * @return -- List<BookDTO> --
     * @author -- 정재익 --
     * @since -- 2월 7일 --
     */
    private List<BookDTO> requestApi(String query, String target, String apiType) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String url;

        if ("kakao".equalsIgnoreCase(apiType)) {
            headers.set("Authorization", "KakaoAK " + kakaoKey);
            url = kakaoUrl + "?query=" + query + "&size=10&target=" + target;
        } else {
            headers.set("X-Naver-Client-Id", clientId);
            headers.set("X-Naver-Client-Secret", clientSecret);
            url = naverUrl + "?query=" + query + "&display=10";
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
                }
        );

        String responseKey = "kakao".equalsIgnoreCase(apiType) ? "documents" : "items";
        Object rawData = Objects.requireNonNull(response.getBody()).get(responseKey);

        if (!(rawData instanceof List<?> listData)) {
            throw new BookException(BookErrorCode.BOOK_NOT_FOUND);
        }

        return listData.stream()
                .map(item -> convertToBookDTO(item, apiType))
                .collect(Collectors.toList());
    }


    /**
     * -- 도서 상세 검색 메소드 --
     *
     * @param isbn      isbn
     * @param sessionId 개인별 session Id
     * @return BookDTO
     * @author 정재익
     * @since 2월 5일
     */
    public BookDTO searchBookDetail(String isbn, String sessionId) {
        List<BookDTO> books = bookCache.get(sessionId);

        if (books == null) {
            throw new BookException(BookErrorCode.BOOK_NOT_FOUND);
        }

        String normalizedIsbn = BookUtil.extractIsbn(isbn);

        return books.stream()
                .filter(book -> book.getIsbn().equalsIgnoreCase(normalizedIsbn))
                .findFirst()
                .orElseThrow(() -> new BookException(BookErrorCode.BOOK_NOT_FOUND));
    }

    /**
     * -- 중복 도서 제거 메소드 --
     * ISBN이 동일한 도서가 있을 경우 하나만 남긴다.
     *
     * @param --List<BookDTO> books 중복이 포함된 도서 리스트--
     * @return List<BookDTO> 중복 제거된 도서 리스트
     * @author 정재익
     * @since 2월 5일
     */
    private List<BookDTO> removeDuplicateBooks(List<BookDTO> books) {
        Set<String> Isbns = new HashSet<>();
        return books.stream()
                .filter(book -> Isbns.add(book.getIsbn()))
                .toList();
    }

    /**
     * -- BookDTO 변환 메소드 --
     * 데이터를 BookDTO로 변환
     *
     * @param -- Object item 데이터 --
     * @param -- String apiType 네이버와 카카오 구분 --
     * @return BookDTO
     * @author 정재익
     * @since 2월 7일
     */
    private BookDTO convertToBookDTO(Object item, String apiType) {
        if ("kakao".equalsIgnoreCase(apiType)) {
            KakaoDTO kakaoBook = objectMapper.convertValue(item, KakaoDTO.class);
            return new BookDTO(
                    kakaoBook.getTitle(),
                    kakaoBook.getAuthor(),
                    kakaoBook.getDescription(),
                    kakaoBook.getImage(),
                    BookUtil.extractIsbn(kakaoBook.getIsbn())
            );
        } else {
            NaverDTO naverBook = objectMapper.convertValue(item, NaverDTO.class);
            return new BookDTO(
                    naverBook.getTitle(),
                    naverBook.getAuthor(),
                    naverBook.getDescription(),
                    naverBook.getImage(),
                    naverBook.getIsbn()
            );
        }
    }


}

//    /**
//     * -- 책 리스트를 DB에 저장하는 메소드 --
//     * 책을 구분하는 고유값인 isbn데이터를 이용하여 이미 존재하는 책은 DB에 저장하지 않음
//     *
//     * @param -- ㅣist<NaverBookVO.Item> items --
//     * @return -- List<Book>
//     * @author -- 정재익 --
//     * @since -- 2월 3일 --
//     */
//    private List<Book> saveBooks(List<NaverBookVO.Item> items) {
//        List<Book> newBooks = items.stream()
//                .map(item -> modelMapper.map(item, Book.class))
//                .filter(book -> !bookRepository.existsByIsbn(book.getIsbn()))
//                .toList();
//
//        return bookRepository.saveAll(newBooks);
//    }

/**
 * -- 책을 찜하거나 찜취소하는 메소드 --
 * 책을 찜하는 기능 이미 찜을 했을 경우 찜 취소
 * Favorite DB에 저장
 * 책이 받은 찜한 수를 Book DB에 최신화
 *
 * @param -- favoriteDTO --
 * @param -- userDetail --
 * @return --GenericResponse<String>--
 * @author -- 정재익 --
 * @since -- 2월 3일 --
 */
//    @Transactional
//    public GenericResponse<String> favoriteBook(FavoriteDTO favoriteDTO, @AuthenticationPrincipal UserDetails userDetails) {
//        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
//        Long memberId = customUserDetails.getId();
//
//        FavoriteId favoriteId = new FavoriteId(userDetails.getUsername(), favoriteDTO.getBookId());
//
//        Book book = bookRepository.findById(favoriteDTO.getBookId())
//                .orElseThrow(() -> new BookException(BookErrorCode.BOOK_NOT_FOUND));
//
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new MemberException(MemberErrorCode.NON_EXISTING_USERNAME));
//
//        if (favoriteRepository.existsById(favoriteId)) {
//            favoriteRepository.deleteById(favoriteId);
//            book.setFavoriteCount(book.getFavoriteCount() - 1);
//            bookRepository.save(book);
//            return GenericResponse.of("찜이 취소되었습니다.");
//        } else {
//            Favorite favorite = modelMapper.map(favoriteDTO, Favorite.class);
//            favorite.setId(favoriteId);
//            favorite.setBook(book);
//            favorite.setMember(member);
//            favoriteRepository.save(favorite);
//            book.setFavoriteCount(book.getFavoriteCount() + 1);
//            bookRepository.save(book);
//
//            return GenericResponse.of("해당 도서를 찜 목록에 추가하였습니다.");
//        }
//    }

/**
 * -- 찜한 책 목록을 확인하는 메소드 --
 * 1. 현재 접속 정보를 바탕으로 멤버ID 추출
 * 2. favoriteRepository에서 해당 멤버가 찜한 책 목록을 반환받는다
 * 3. favorite 리스트를 BookSimpleDto로 변환한 뒤 반환한다.
 *
 * @param -- userDetail --
 * @return -- List<BookSimpleDTO> --
 * @author -- 정재익 --
 * @since -- 2월 3일 --
 */
//    public List<BookDTO> searchFavoriteBooks(@AuthenticationPrincipal UserDetails userDetails) {
//        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
//        Long memberId = customUserDetails.getId();
//        String memberUsername = userDetails.getUsername();
//
//        if (!memberRepository.existsById(memberId)) {
//            throw new MemberException(MemberErrorCode.NON_EXISTING_USERNAME);
//        }
//
//        List<Favorite> favorites = favoriteRepository.findById_MemberUsername(memberUsername);
//
//        if (favorites.isEmpty()) {
//            throw new BookException(BookErrorCode.NO_FAVORITE_BOOKS);
//        }
//
//        return favorites.stream()
//                .map(favorite -> modelMapper.map(favorite.getBook(), BookDTO.class))
//                .collect(Collectors.toList());
//    }
//}
