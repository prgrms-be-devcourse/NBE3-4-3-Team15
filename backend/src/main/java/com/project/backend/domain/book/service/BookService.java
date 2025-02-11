package com.project.backend.domain.book.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.backend.domain.book.dto.BookDTO;
import com.project.backend.domain.book.dto.KakaoDTO;
import com.project.backend.domain.book.dto.NaverDTO;
import com.project.backend.domain.book.entity.Book;
import com.project.backend.domain.book.entity.Favorite;
import com.project.backend.domain.book.exception.BookErrorCode;
import com.project.backend.domain.book.exception.BookException;
import com.project.backend.domain.book.key.FavoriteId;
import com.project.backend.domain.book.repository.BookRepository;
import com.project.backend.domain.book.repository.FavoriteRepository;
import com.project.backend.domain.book.util.BookUtil;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.exception.MemberErrorCode;
import com.project.backend.domain.member.exception.MemberException;
import com.project.backend.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
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
     * 2. Page<BookDto>로 변환하여 반환
     *
     * @param -- query (검색어) --
     * @param -- page 페이지 --
     * @param -- size 한 페이지에 보여주는 책 수량 --
     * @return -- Page<BookDTO> --
     * @author -- 정재익 --
     * @since -- 2월 10일 --
     */
    public Page<BookDTO> searchBooks(String query, int page, int size) {
        if (!StringUtils.hasText(query)) {
            throw new BookException(BookErrorCode.QUERY_EMPTY);
        }

        List<BookDTO> allBooks = new ArrayList<>();

        allBooks.addAll(requestApi(query, "naver", 1));
        allBooks.addAll(requestApi(query, "naver", 100));
        allBooks.addAll(requestApi(query, "kakao", 0));


        List<BookDTO> uniqueBooks = removeDuplicateBooks(allBooks);

        Pageable pageable = PageRequest.of(page, size);
        int start = page * size;

        if (start >= uniqueBooks.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, uniqueBooks.size());
        }

        int end = Math.min(start + size, uniqueBooks.size());
        List<BookDTO> pagedBooks = uniqueBooks.subList(start, end);

        return new PageImpl<>(pagedBooks, pageable, uniqueBooks.size());
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
    private List<BookDTO> requestApi(String query, String apiType, int naverStart) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String url;

        if ("kakao".equalsIgnoreCase(apiType)) {
            headers.set("Authorization", "KakaoAK " + kakaoKey);
            url = String.format("%s?query=%s&target=author&page=4&size=50",
                    kakaoUrl, query);
        } else {
            headers.set("X-Naver-Client-Id", clientId);
            headers.set("X-Naver-Client-Secret", clientSecret);


            url = String.format("%s?query=%s&display=100&start=%d",
                    naverUrl, query, naverStart);
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
     * @author -- 정재익, 김남우 --
     * @since -- 2월 9일 --
     */
    @Transactional
    public boolean favoriteBook(BookDTO bookDto, String username) {

        if (!bookRepository.existsByIsbn(bookDto.getIsbn())) {
            bookRepository.save(Book.builder()
                    .title(bookDto.getTitle())
                    .author(bookDto.getAuthor())
                    .description(bookDto.getDescription())
                    .image(bookDto.getImage())
                    .isbn(bookDto.getIsbn())
                    .favoriteCount(bookDto.getFavoriteCount())
                    .build());
        }

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NON_EXISTING_USERNAME));

        FavoriteId favoriteId = new FavoriteId(member.getId(), bookRepository.findByIsbn(bookDto.getIsbn()).getId());

        Book book = bookRepository.findByIsbn(bookDto.getIsbn());

        if (favoriteRepository.existsById(favoriteId)) {
            favoriteRepository.deleteById(favoriteId); // 먼저 favorite 테이블에서 삭제

            int favoriteCount = book.getFavoriteCount();
            if (favoriteCount == 1) {
                bookRepository.delete(book); // favoriteCount가 1이면 Book 테이블에서 도서 삭제
            }
            else {
                bookRepository.updateFavoriteCount(book, -1); // 아니면 favoriteCount 감소
            }

            return false;
        }

        else {
            bookRepository.updateFavoriteCount(book, +1); // favoriteCount 1 증가

            Favorite favorite = Favorite.builder()
                    .id(favoriteId)
                    .book(book)
                    .member(member)
                    .build();

            favoriteRepository.save(favorite); // favorite 테이블에 저장

            return true;
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