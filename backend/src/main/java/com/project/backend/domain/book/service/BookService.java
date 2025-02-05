package com.project.backend.domain.book.service;

import com.project.backend.domain.book.dto.BookDTO;
import com.project.backend.domain.book.dto.BookSimpleDTO;
import com.project.backend.domain.book.dto.FavoriteDTO;
import com.project.backend.domain.book.entity.Book;
import com.project.backend.domain.book.entity.Favorite;
import com.project.backend.domain.book.exception.BookErrorCode;
import com.project.backend.domain.book.exception.BookException;
import com.project.backend.domain.book.key.FavoriteId;
import com.project.backend.domain.book.repository.BookRepository;
import com.project.backend.domain.book.repository.FavoriteRepository;
import com.project.backend.domain.book.vo.KakaoBookVO;
import com.project.backend.domain.book.vo.NaverBookVO;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * -- 도서 관련 작업을 처리하는 서비스클래스 --
 *
 * @author -- 정재익 --
 * @since -- 1월 27일 --
 */
@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

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
     * -- 네이버api를 통해 받아온 검색 결과를 List<BookSimpleDto>로 변환하여 컨트롤러에 반환하는 메소드 --
     * -- 동시에 아래 saveBooks 메소드를 통해 검색 결과를 DB에 저장
     * <p>
     * 1. 검색 결과를 BookDataFromApi에 전달
     * 2. 검색 결과를 DB에 저장
     * 3. DB에 저장된 값을 List<BookSimpleDto>로 변환하여 컨트롤러에 전달
     *
     * @param -- title (컨트롤러에서 입력한 검색어) --
     * @return -- List<BookSimpleDTO> --
     * @author -- 정재익 --
     * @since -- 2월 3일 --
     */
    public List<BookSimpleDTO> searchBooks(String query, boolean isAuthorSearch) {

        if (query == null || query.isEmpty()) {
            throw new BookException(BookErrorCode.INVALID_SORT_PROPERTY);
        }

        if (isAuthorSearch) {
            List<KakaoBookVO.Item> items = searchKakaoBooks(query);
            return items.stream()
                    .map(item -> modelMapper.map(item, BookSimpleDTO.class))
                    .toList();
        } else {
            searchKakaoBooks(query);
            searchNaverBooks(query);

            return null;
        }
    }

    /**
     * -- 네이버api를 통해 검색어에 대한 도서데이터를 가져오는 메소드 --
     * 책은 한번에 30권 조회되도록 설정했다.
     * <p>
     *
     * @param -- title (컨트롤러에서 입력한 검색어) --
     * @return -- List<NaverBookVO.Item> --
     * @author -- 정재익 --
     * @since -- 2월 3일 --
     */
    private List<NaverBookVO.Item> searchNaverBooks(String title) {
        if (title == null || title.isEmpty()) {
            throw new BookException(BookErrorCode.BOOK_NOT_FOUND);
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        String url = apiUrl + "?query=" + title + "&display=30";
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<NaverBookVO> response = restTemplate.exchange(url, HttpMethod.GET, entity, NaverBookVO.class);

        return Optional.ofNullable(response.getBody())
                .map(NaverBookVO::getItems)
                .orElseThrow(() -> new BookException(BookErrorCode.BOOK_NOT_FOUND));
    }

    /**
     * -- 카카오 도서 검색 API를 호출하여 도서 목록을 조회하는 메소드 --
     *
     * @param query 검색어
     * @return 카카오 API로부터 받은 도서 목록을 담은 allKakaoBooks 리스트
     *
     * @author 김남우
     * @since 2025년 1월 27일
     */
    private List<KakaoBookVO.Item> searchKakaoBooks(String query) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoKey);

        List<KakaoBookVO.Item> allKakaoBooks = new ArrayList<>();

        for (int page = 1; page <= 1; page++) {
            String url = kakaoUrl + "?query=" + query + "&page=" + page + "&size=10";

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<KakaoBookVO> response = restTemplate.exchange(url, HttpMethod.GET, entity, KakaoBookVO.class);

            List<KakaoBookVO.Item> kakaoBooks = response.getBody().getItems();
            allKakaoBooks.addAll(kakaoBooks);
        }

        return allKakaoBooks;
    }

    /**
     * -- 책 리스트를 DB에 저장하는 메소드 --
     * 책을 구분하는 고유값인 isbn데이터를 이용하여 이미 존재하는 책은 DB에 저장하지 않음
     *
     * @param -- ㅣist<NaverBookVO.Item> items --
     * @return -- List<Book>
     * @author -- 정재익 --
     * @since -- 2월 3일 --
     */
    private List<Book> saveBooks(List<NaverBookVO.Item> items) {
        List<Book> newBooks = items.stream()
                .map(item -> modelMapper.map(item, Book.class))
                .filter(book -> !bookRepository.existsByIsbn(book.getIsbn()))
                .toList();

        return bookRepository.saveAll(newBooks);
    }

    /**
     * -- DB에 있는 책을 컨트롤러에 전달하는 메소드 --
     * 1. List<Book>형태로 있는 데이터를 List<BookSimpleDto>로 변환한 후 컨트롤러에 전달
     * 2. 상세 조회가 아니기 때문에 설명 데이터는 전달되지 않음
     * 3. 정렬조건과 오름차순,내림차순이 입력될시에 정렬기능 작동
     *
     * @return -- List<BookSimpleDto> --
     * @author -- 정재익 --
     * @since -- 1월 27일 --
     */
    public List<BookSimpleDTO> searchAllBooks(String sortBy, String direction) {
        try {
            Sort sort = Sort.by(direction.equalsIgnoreCase("desc") ? Sort.Order.desc(sortBy) : Sort.Order.asc(sortBy));
            List<Book> books = bookRepository.findAll(sort);

            if (books.isEmpty()) {
                throw new BookException(BookErrorCode.BOOK_DB_EMPTY);
            }

            return books.stream().map(book -> modelMapper.map(book, BookSimpleDTO.class)).toList();
        } catch (PropertyReferenceException e) {
            throw new BookException(BookErrorCode.INVALID_SORT_PROPERTY);
        }
    }

    /**
     * -- 책의 상세정보를 반환하는 메서드 --
     *
     * @param -- id --
     * @return -- BookDto --
     * @author -- 정재익 --
     * @since -- 2월 3일 --
     */
    public BookDTO searchDetailsBook(Long id) {
        Optional<Book> book = bookRepository.findById(id);

        return book.map(b -> modelMapper.map(b, BookDTO.class)).orElseThrow(() -> new BookException(BookErrorCode.BOOK_NOT_FOUND));
    }

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
    public List<BookSimpleDTO> searchFavoriteBooks(@AuthenticationPrincipal UserDetails userDetails) {
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
                .map(favorite -> modelMapper.map(favorite.getBook(), BookSimpleDTO.class))
                .collect(Collectors.toList());
    }

}