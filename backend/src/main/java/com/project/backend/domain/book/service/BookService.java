package com.project.backend.domain.book.service;

import com.project.backend.domain.book.dto.BookSimpleDto;
import com.project.backend.domain.book.entity.Book;
import com.project.backend.domain.book.repository.BookRepository;
import com.project.backend.domain.book.vo.NaverBookVo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ModelMapper modelMapper;


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
     *
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

    private NaverBookVo BookDataFromApi(String title) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        String url = apiUrl + "?query=" + title + "&display=30";

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<NaverBookVo> response = restTemplate.exchange(url, HttpMethod.GET, entity, NaverBookVo.class);

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

    public List<BookSimpleDto> searchTitleBooks(String title) {
        NaverBookVo naverBookVo = BookDataFromApi(title);

        if (naverBookVo != null && naverBookVo.getItems() != null) {
            List<Book> books = naverBookVo.getItems().stream().map(item -> modelMapper.map(item, Book.class)).collect(Collectors.toList());

            saveBooks(books);
        }

        return naverBookVo.getItems().stream().map(item -> modelMapper.map(item, BookSimpleDto.class)).toList();

    }

    /**
     * -- 책 리스트를 DB에 저장하는 메소드 --
     * 현재 동일한 책도 저장되는 에러가 있어서 수정해야함.
     * 책을 구분하는 고유값인 isbn데이터를 추가로 받아와서 그것으로 중복책을 판별해야함
     *
     * @param -- List<Book> books --
     * @author -- 정재익 --
     * @since -- 1월 25일 --
     */

    private void saveBooks(List<Book> books) {
        List<Book> newBooks = books.stream().filter(book -> !bookRepository.existsById(book.getId())).collect(Collectors.toList());

        bookRepository.saveAll(newBooks);
    }


    /**
     * -- DB에 있는 책을 컨트롤러에 전달하는 메소드 --
     * 1. List<Book>형태로 있는 데이터를 List<BookSimpleDto>로 변환한 후 컨트롤러에 전달
     * 2. 상세 조회가 아니기 때문에 설명 데이터는 전달되지 않음
     *
     * @return -- List<BookSimpleDto> --
     * @author -- 정재익 --
     * @since -- 1월 25일 --
     */

    public List<BookSimpleDto> searchAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(book -> modelMapper.map(book, BookSimpleDto.class)).toList();
    }


}