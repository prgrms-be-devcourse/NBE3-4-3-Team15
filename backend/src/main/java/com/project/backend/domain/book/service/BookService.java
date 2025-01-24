package com.project.backend.domain.book.service;

import com.project.backend.domain.book.vo.NaverBookVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BookService {

    /**
     * -- 네이버도서 api에 데이터를 요청하기 위한 헤더값과 url --
     *
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
     * -- 네이버에서 검색에 대한 도서를 가져오는 메서드 --
     *
     * 1. HttpHeader에 클라이언트ID와 비밀번호를 삽입한다
     * 2. 요청 url에 검색어를 더해서 네이버에 요청할 url을 완성시킨다
     * 3. HttpEntity를 이용하여 헤더값을 삽입한다.
     * 4. RestTemplate에 만들어뒀던 HttpEntity를 넣어 네이버 api에게 GET요청한여 결과를 ResponseEntity에 저장한다
     * 5. ResponseEntity의 Body부분에는 검색결과가 담겨져있다 그 부분만 떼어내어 리턴한다.
     *
     * @param -- query (컨트롤러에서 입력한 검색어) --
     *
     * @return -- NaverBookVo --
     *
     * @author -- 정재익 --
     * @since -- 1월 24일 --
     */
    public NaverBookVo searchBooks(String query) {
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        // 요청 파라미터 설정
        String url = apiUrl + "?query=" + query;

        // 요청 보내기
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<NaverBookVo> response = restTemplate.exchange(url,
                org.springframework.http.HttpMethod.GET, entity, NaverBookVo.class);

        return response.getBody();
    }
}
