package com.project.backend.domain.book.service;

import com.project.backend.domain.book.dto.BookDTO;
import com.project.backend.domain.book.dto.FavoriteDTO;
import com.project.backend.domain.book.entity.Book;
import com.project.backend.domain.book.entity.Favorite;
import com.project.backend.domain.book.exception.BookErrorCode;
import com.project.backend.domain.book.exception.BookException;
import com.project.backend.domain.book.key.FavoriteId;
import com.project.backend.domain.book.repository.BookRepository;
import com.project.backend.domain.book.repository.FavoriteRepository;
import com.project.backend.domain.book.vo.ApiBookVO;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.exception.MemberErrorCode;
import com.project.backend.domain.member.exception.MemberException;
import com.project.backend.domain.member.repository.MemberRepository;
import com.project.backend.global.authority.CustomUserDetails;
import com.project.backend.global.response.GenericResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final ModelMapper modelMapper;
    private final ConcurrentHashMap<String, List<BookDTO>> bookCache = new ConcurrentHashMap<>();


    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.book-search-url}")
    private String apiUrl;

    @Value("${kakao.key}")
    private String kakaoKey;

    @Value("${kakao.url}")
    private String kakaoUrl;

    /**
     * -- 도서 검색 메소드 --
     * 1. 제목 검색일 경우 네이버와 카카오 Api에 요청
     * 2. 작가 검색일 경우 카카오 Api에 요청
     * 3. List<BookDto>로 변환하여 반환
     *
     * @param -- title (검색어) --
     * @param -- isAuthorSearch (작가검색, 도서검색 판단) --
     * @param -- sessionId (개인별 세션 ID)
     *
     * @return -- List<BookDTO> --
     * @author -- 정재익 --
     * @since -- 2월 5일 --
     */
    public List<BookDTO> searchBooks(String query, boolean isAuthorSearch, String sessionId) {

        if (query == null || query.isEmpty()) {
            throw new BookException(BookErrorCode.INVALID_SORT_PROPERTY);
        }

        List<ApiBookVO> allBooks = new ArrayList<>();

        if (isAuthorSearch) {
            allBooks.addAll(requestKakaoApi(query, "person"));
        } else {
            allBooks.addAll(requestKakaoApi(query, "title"));
            allBooks.addAll(requestNaverApi(query));
        }

        List<BookDTO> bookList = allBooks.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .toList();

        bookCache.put(sessionId, bookList);

        return bookList;
    }

    /**
     * -- 네이버 Api 요청 메소드 --
     * 책은 한번에 10권 조회하도록 설정했다.
     *
     * @param -- title (컨트롤러에서 입력한 검색어) --
     * @return -- List<ApiBookVo> --
     * @author -- 정재익 --
     * @since -- 2월 5일 --
     */
    private List<ApiBookVO> requestNaverApi(String title) {
        if (title == null || title.isEmpty()) {
            throw new BookException(BookErrorCode.BOOK_NOT_FOUND);
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        String url = apiUrl + "?query=" + title + "&display=10";
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, List<Map<String, Object>>>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

        List<Map<String, Object>> items = Objects.requireNonNull(response.getBody()).getOrDefault("items", new ArrayList<>());

        return items.stream()
                .map(item -> modelMapper.map(item, ApiBookVO.class))
                .toList();

    }

    /**
     * -- 카카오 Api 요청 메소드 --
     *
     * @param query 검색어
     * @param target 도서 검색 필터'
     * @return List<ApiBookVo>
     *
     * @author 정재익
     * @since 2월 5일
     */
    private List<ApiBookVO> requestKakaoApi(String query, String target) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoKey);

        String url = kakaoUrl + "?query=" + query + "&size=10" + "&target=" + target;
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, List<Map<String, Object>>>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

        List<Map<String, Object>> documents = Objects.requireNonNull(response.getBody()).getOrDefault("documents", new ArrayList<>());

        return documents.stream()
                .map(doc -> modelMapper.map(doc, ApiBookVO.class))
                .toList();
    }

    /**
     * -- 도서 상세 검색 메소드 --
     *
     * @param isbn isbn
     * @param sessionId 개인별 session Id
     * @return BookDTO
     *
     * @author 정재익
     * @since 2월 5일
     */
    public BookDTO searchBookDetail(String isbn, String sessionId) {
        List<BookDTO> books = bookCache.get(sessionId);

        if (books == null) {
            throw new BookException(BookErrorCode.BOOK_NOT_FOUND);
        }

        return books.stream()
                .filter(book -> book.getIsbn().equalsIgnoreCase(isbn))
                .findFirst()
                .orElseThrow(() -> new BookException(BookErrorCode.BOOK_NOT_FOUND));
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
    @Transactional
    public GenericResponse<String> favoriteBook(FavoriteDTO favoriteDTO, @AuthenticationPrincipal UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long memberId = customUserDetails.getId();

        FavoriteId favoriteId = new FavoriteId(userDetails.getUsername(), favoriteDTO.getBookId());

        Book book = bookRepository.findById(favoriteDTO.getBookId())
                .orElseThrow(() -> new BookException(BookErrorCode.BOOK_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NON_EXISTING_ID));

        if (favoriteRepository.existsById(favoriteId)) {
            favoriteRepository.deleteById(favoriteId);
            book.setFavoriteCount(book.getFavoriteCount() - 1);
            bookRepository.save(book);
            return GenericResponse.of("찜이 취소되었습니다.");
        } else {
            Favorite favorite = modelMapper.map(favoriteDTO, Favorite.class);
            favorite.setId(favoriteId);
            favorite.setBook(book);
            favorite.setMember(member);
            favoriteRepository.save(favorite);
            book.setFavoriteCount(book.getFavoriteCount() + 1);
            bookRepository.save(book);

            return GenericResponse.of("해당 도서를 찜 목록에 추가하였습니다.");
        }
    }

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
    public List<BookDTO> searchFavoriteBooks(@AuthenticationPrincipal UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long memberId = customUserDetails.getId();
        String memberUsername = userDetails.getUsername();

        if (!memberRepository.existsById(memberId)) {
            throw new MemberException(MemberErrorCode.NON_EXISTING_ID);
        }

        List<Favorite> favorites = favoriteRepository.findById_MemberUsername(memberUsername);

        if (favorites.isEmpty()) {
            throw new BookException(BookErrorCode.NO_FAVORITE_BOOKS);
        }

        return favorites.stream()
                .map(favorite -> modelMapper.map(favorite.getBook(), BookDTO.class))
                .collect(Collectors.toList());
    }

}