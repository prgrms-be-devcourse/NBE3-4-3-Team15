package com.project.backend.domain.book.service;

import com.project.backend.domain.book.dto.BookDTO;
import com.project.backend.domain.book.dto.BookSimpleDTO;
import com.project.backend.domain.book.dto.FavoriteDTO;
import com.project.backend.domain.book.entity.Book;
import com.project.backend.domain.book.entity.Favorite;
import com.project.backend.domain.book.key.FavoriteId;
import com.project.backend.domain.book.repository.BookRepository;
import com.project.backend.domain.book.repository.FavoriteRepository;
import com.project.backend.domain.book.vo.NaverBookVO;
import com.project.backend.global.exception.GlobalErrorCode;
import com.project.backend.global.exception.GlobalException;
import com.project.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    /**
     * -- 네이버도서 api에 데이터를 요청하기 위한 헤더값과 url --
     * <p>
     * clientId = application.yml에 존재하는 네이버클라이언트 아이디 http 헤더에 삽입한다
     * clientSecret = application.yml에 존재하는 네이버클라이언트 비밀번호 http 헤더에 삽입한다
     * apiUrl = application.yml에 존재하는 api 요청 url
     *
     * @author -- 정재익 --
     * @since -- 1월 24일 --
     */
    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.book-search-url}")
    private String apiUrl;

    /**
     * -- 네이버api를 통해 검색어에 대한 도서데이터를 가져오는 메소드 --
     * 책은 한번에 30권 조회되도록 설정했다.
     * <p>
     * 1. HttpHeader에 클라이언트ID와 비밀번호를 삽입한다
     * 2. 요청 url에 검색어를 더해서 네이버에 요청할 url을 완성시킨다
     * 3. HttpEntity를 이용하여 헤더값을 삽입한다.
     * 4. RestTemplate에 만들어뒀던 HttpEntity를 넣어 네이버 api에게 GET요청한여 결과를 ResponseEntity<NaverBookVo>에 저장한다
     * 5. ResponseEntity의 Body부분에는 검색결과가 담겨져있다 그 부분을 반환한다
     *
     * @param -- title (컨트롤러에서 입력한 검색어) --
     * @return -- NaverBookVo --
     * @author -- 정재익 --
     * @since -- 1월 25일 --
     */
    private NaverBookVO BookDataFromApi(String title) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        String url = apiUrl + "?query=" + title + "&display=30";

        if (title == null || title.isEmpty()) {
            throw new GlobalException(GlobalErrorCode.BOOK_NOT_FOUND);
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<NaverBookVO> response = restTemplate.exchange(url, HttpMethod.GET, entity, NaverBookVO.class);

        return response.getBody();
    }

    /**
     * -- 네이버api를 통해 받아온 검색 결과를 List<BookSimpleDto>로 변환하여 컨트롤러에 반환하는 메소드 --
     * -- 동시에 아래 saveBooks 메소드를 통해 검색 결과를 DB에 저장
     * <p>
     * 1. 검색 결과를 naverBook에 삽입
     * 2. 검색 결과가 존재하면 List<Book>으로 변환하여 DB에 저장
     * 3. 검색 결과를 List<BookSimpleDto>로 변환하여 컨트롤러에 전달
     * 4. 책 상세 조회가 아니기 때문에 책 설명은 DB에는 저장되지만 결과로 전달되지 않음
     *
     * @param -- title (컨트롤러에서 입력한 검색어) --
     * @return -- List<NaverBookVo> --
     * @author -- 정재익 --
     * @since -- 1월 25일 --
     */
    public List<BookSimpleDTO> searchTitleBooks(String title) {
        NaverBookVO naverBookVo = BookDataFromApi(title);

        if (naverBookVo != null && naverBookVo.getItems() != null) {
            List<Book> books = naverBookVo.getItems().stream().map(item -> modelMapper.map(item, Book.class)).collect(Collectors.toList());
            saveBooks(books);
        } else {
            throw new GlobalException(GlobalErrorCode.BOOK_NOT_FOUND);
        }

        return naverBookVo.getItems().stream().map(item -> modelMapper.map(item, BookSimpleDTO.class)).toList();
    }

    /**
     * -- 책 리스트를 DB에 저장하는 메소드 --
     * 책을 구분하는 고유값인 isbn데이터를 이용하여 이미 존재하는 책은 DB에 저장하지 않음
     *
     * @param -- List<Book> books --
     * @author -- 정재익 --
     * @since -- 1월 26일 --
     */
    private void saveBooks(List<Book> books) {
        List<Book> saveBooks = books.stream().filter(book -> !bookRepository.existsByIsbn(book.getIsbn())).collect(Collectors.toList());

        bookRepository.saveAll(saveBooks);
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
            Sort.Order order = direction.equalsIgnoreCase("desc")
                    ? Sort.Order.desc(sortBy)
                    : Sort.Order.asc(sortBy);
            Sort sort = Sort.by(order);

            List<Book> books = bookRepository.findAll(sort);

            if (books.isEmpty()) {
                throw new GlobalException(GlobalErrorCode.BOOK_DB_EMPTY);
            }

            return books.stream().map(book -> modelMapper.map(book, BookSimpleDTO.class)).toList();

        } catch (PropertyReferenceException e) {
            throw new GlobalException(GlobalErrorCode.INVALID_SORT_PROPERTY);
        }
    }

    /**
     * -- 책의 상세정보를 반환하는 메서드 --
     * 책을 구분하는 고유값인 isbn데이터를 이용하여 이미 존재하는 책은 DB에 저장하지 않음
     * 책의 추천받은 개수는 favoriteRepository 에서 받아와서 Dto에 추가후 db에 저장
     * 해당 id의 책이 없으면 예외 처리
     *
     * @param -- id (책의 id) --
     * @return -- BookDto --
     * @author -- 정재익 --
     * @since -- 1월 27일 --
     */
    public BookDTO searchDetailsBook(int id) {
        Optional<Book> book = bookRepository.findById(id);

        return book.map(b -> {
            BookDTO bookDto = modelMapper.map(b, BookDTO.class);

            int favoriteCount = favoriteRepository.countByIdBookId(id);

            b.setFavoriteCount(favoriteCount);
            bookRepository.save(b);
            return bookDto;
        }).orElseThrow(() -> new GlobalException(GlobalErrorCode.BOOK_NOT_FOUND));
    }

    /**
     * -- 책을 찜하거나 찜취소하는 메소드 --
     * 1. favoriteDTO의 멤버id와 책id로 복합키 구현
     * 2. 복합키의 고유성을 이용하여 해당 복합키의 존재유무 확인
     * 3. 이미 존재할 시 (이미 찜을 했을 경우) 찜 취소
     * 4. 존재 하지 않을 시 책id와 멤버id로 멤버와 책 객체 가져옴 (이 과정에서 memberRepository에 직접 접근하는데 member가 구현되면 수정해야함)
     * 5. Dto를 Favorite로 변환하고 명시적 복합키 지정후에 DB에 저장
     *
     * @param -- favoriteDTO --
     * @return --GenericResponse<String>--
     * @author -- 정재익 --
     * @since -- 1월 28일 --
     */
    public GenericResponse<String> favoriteBook(FavoriteDTO favoriteDTO) {
        FavoriteId favoriteId = new FavoriteId(favoriteDTO.getMemberId(), favoriteDTO.getBookId());

        if (favoriteRepository.existsById(favoriteId)) {
            favoriteRepository.deleteById(favoriteId);
            return GenericResponse.of("찜이 취소되었습니다.");
        } else {
            Book book = bookRepository.findById(favoriteDTO.getBookId())
                    .orElseThrow(() -> new GlobalException(GlobalErrorCode.BOOK_NOT_FOUND));
            Member member = memberRepository.findById(favoriteDTO.getMemberId())
                    .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));

            Favorite favorite = modelMapper.map(favoriteDTO, Favorite.class);

            favorite.setBook(book);
            favorite.setMember(member);

            return GenericResponse.of("해당 도서를 찜 목록에 추가하였습니다.");
        }
    }

    /**
     * -- 찜한 책 목록을 확인하는 메소드 --
     * 1. memberDto를 받아와서 memberId를 추출 (아직 memberDto가 구현되지 앟음)
     * 2. favoriteRepository에서 해당 멤버가 찜한 책 목록을 반환받는다
     * 3. favorite 리스트를 BookSimpleDto로 변환한 뒤 반환한다.
     *
     * @param -- MemberDTO  --
     * @return -- List<BookSimpleDTO> --
     * @author -- 정재익 --
     * @since -- 1월 27일 --
     */
    public List<BookSimpleDTO> searchFavoriteBooks(MemberDTO memberDto) {
        String memberId = memberDto.getMemberId();

        if (!memberRepository.existsById(memberId)) {
            throw new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND);
        }

        List<Favorite> favorites = favoriteRepository.findByIdMemberId(memberId);

        if (favorites.isEmpty()) {
            throw new GlobalException(GlobalErrorCode.NO_FAVORITE_BOOKS);
        }

        return favorites.stream()
                .map(favorite -> modelMapper.map(favorite.getBook(), BookSimpleDTO.class))
                .collect(Collectors.toList());
    }
}


