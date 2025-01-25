package com.project.backend.domain.book.service;

import com.project.backend.domain.book.entity.Book;
import com.project.backend.domain.book.repository.BookRepository;
import com.project.backend.domain.book.vo.NaverBookVo;
import com.project.backend.global.converter.BookConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookConverter bookConverter;

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
     * -- 네이버에서 검색에 대한 도서를 가져오고 데이터 베이스에 저장하는 메서드 --
     * <p>
     * 1. HttpHeader에 클라이언트ID와 비밀번호를 삽입한다
     * 2. 요청 url에 검색어를 더해서 네이버에 요청할 url을 완성시킨다
     * 3. HttpEntity를 이용하여 헤더값을 삽입한다.
     * 4. RestTemplate에 만들어뒀던 HttpEntity를 넣어 네이버 api에게 GET요청한여 결과를 ResponseEntity에 저장한다
     * 5. ResponseEntity의 Body부분에는 검색결과가 담겨져있다 그 부분만 떼어낸다
     * 6. 만약 응답이 null이 아니라면 컨버터를 이용하여 호출결과를 book의 리스트로 만든다.
     * 7. book의 리스트를 데이터베이스에 저장한다 이때 중복을 막기위해 existById를 활용한다.
     * 8. 떼어낸 검색결과 부분을 반환한다 이때 NaverBookVo의 설명값 (access = JsonProperty.Access.WRITE_ONLY)의 효과로 설명부분은 떼어내고 반환한다.
     * 9. 이 설명부분은 데이터베이스에는 저장된다 설명부분은 도서 상세 조회에 활용할 예정이다.
     *
     * @param -- title (컨트롤러에서 입력한 검색어) --
     * @return -- NaverBookVo --
     * @author -- 정재익 --
     * @since -- 1월 25일 --
     */
    public NaverBookVo searchBooks(String title) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        String url = apiUrl + "?query=" + title + "&display=30";

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<NaverBookVo> response = restTemplate.exchange(url,
                org.springframework.http.HttpMethod.GET, entity, NaverBookVo.class);

        NaverBookVo naverBookVo = response.getBody();

        if (naverBookVo != null && naverBookVo.getItems() != null) {
            List<Book> books = bookConverter.apiToListBook(naverBookVo.getItems());

            for (Book book : books) {
                if (!bookRepository.existsById(book.getId())) {
                    bookRepository.save(book);
                }
            }
        }
        return naverBookVo;
    }
}