package com.project.backend.domain.book.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.backend.domain.book.dto.BookDTO
import com.project.backend.domain.book.dto.KakaoDTO
import com.project.backend.domain.book.dto.NaverDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

/**
 * -- API 서비스 클래스 --
 *
 * @author -- 정재익 --
 * @since -- 3월 04일 --
 */
@Service
class ApiClientService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
    @Value("\${naver.client-id}") val clientId: String,
    @Value("\${naver.client-secret}") val clientSecret: String,
    @Value("\${naver.book-search-url}") val naverUrl: String,
    @Value("\${kakao.key}") val kakaoKey: String,
    @Value("\${kakao.url}") val kakaoUrl: String
) {

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
    fun requestApi(query: String, apiType: String, naverStart: Int, kakaoPage: Int): List<BookDTO> {
        val (headers, url, responseKey) = getApiRequestParams(query, apiType, naverStart, kakaoPage)
        val entity = HttpEntity<String>(headers)

        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            object : ParameterizedTypeReference<Map<String, Any>>() {})
        val rawData = (response.body?.get(responseKey) as? List<*>)?.filterNotNull() ?: emptyList()

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
            id = null,
            title = bookDto.title,
            author = bookDto.author,
            description = bookDto.description,
            image = bookDto.image,
            isbn = bookDto.isbn,
            ranking = null,
            favoriteCount = 0
        )
    }


}
