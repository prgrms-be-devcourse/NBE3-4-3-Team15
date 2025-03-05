package com.project.backend.domain.book.service

import com.project.backend.domain.book.dto.BookDTO
import com.project.backend.domain.book.repository.RedisRepository
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Safelist
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.security.MessageDigest

/**
 * -- 크롤링 서비스 --
 *
 * @author -- 정재익 --
 * @since -- 3월 01일 --
 */
@Service
class CrawlingService(private val bookService: BookService, private val redisRepository : RedisRepository) {
    private val logger = LoggerFactory.getLogger(CrawlingService::class.java)

    /**
     * -- 크롤링 전체 통제 메서드 --
     * Redis에 저장한 이전 해시값과 현재 해시값을 비교해 베스트셀러가 바뀌었는지 검사
     * 바뀌지 않았으면 상세페이지 크롤링 실행
     *
     * @author -- 정재익 --
     * @since -- 3월 02일 --
     */
    fun main() = runBlocking {
        val targetUrl = "https://www.yes24.com/Product/Category/RealTimeBestSeller?categoryNumber=001"

        val doc = Jsoup.connect(targetUrl).get()
        val bestSellerMaps = getBestSellerMaps(doc)
        val currentHash = getMapHash(bestSellerMaps)
        val previousHash = redisRepository.loadPreviousHash()

        if (currentHash != previousHash) {
            val bestSellerBookDTOs = getBestSellerBookDTOs(bestSellerMaps)
            bookService.saveBestsellers(bestSellerBookDTOs)
            redisRepository.saveHash(currentHash)
        } else {
            logger.info("베스트셀러 변경 없음. 크롤링 생략")
        }
    }

    /**
     * -- Map을 해싱하는 메서드 --
     * @param -- map 베스트셀러 순위와 링크가 담김 --
     * @return -- String 해시 값--
     * @author -- 정재익 --
     * @since -- 3월 03일 --
     */
    fun getMapHash(map: Map<Int, String>): String {
        val mapString = map.toSortedMap().entries.joinToString { "${it.key}:${it.value}" }

        return MessageDigest.getInstance("SHA-256")
            .digest(mapString.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    /**
     * -- 베스트셀러 목록 순위와 링크를 가져오는 메소드 --
     *
     *
     * @return -- Map<Int, String> int는 순위 string은 링크 --
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    private fun getBestSellerMaps(doc: Document): Map<Int, String> {
        val baseUrl = "https://www.yes24.com"

        val bestSellerLinks =
            doc.select("#yesBestList > li > div > div.item_info > div.info_row.info_name > a.gd_name").eachAttr("href")

        return bestSellerLinks.mapIndexed { index, href ->
            (index + 1) to "$baseUrl$href"
        }.toMap()
    }

    /**
     * -- 베스트셀러 정보를 가져오는 것을 코루틴으로 설정하는 메소드 --
     *
     * @param -- bestSellersMaps 순위와 책 링크가 들어있는 Map --
     * @return -- List<BookDTO> 베스트셀러 정보가 입력된 책 리스트 --
     *
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    private suspend fun getBestSellerBookDTOs(bestSellersMaps: Map<Int, String>): List<BookDTO> =
        coroutineScope {
            bestSellersMaps.map { (ranking, link) ->
                async(Dispatchers.IO) {
                    getBestSellerBookDTO(ranking, link)
                }
            }.awaitAll()
        }

    /**
     * -- 베스트셀러 정보를 가져오고 BookDTO에 입력하는 메서드 --
     *
     * 만약 크롤링 중 요소에 값이 들어가지 않은 경우가 있으면 실제로 해당 데이터가 없을수도 있겠지만 빠른 크롤링으로 인해 데이터가 미처 로드되지 못했을 경우를 고려하여 총 3회까지 크롤링
     * 3회 크롤링을 했지만 요소에 값이 들어가지 않으면 해당 데이터가 없다고 판단하여 있는것들만 반환
     * 1회 크롤링으로 모든 요소에 값이 들어가면 추가 크롤링 하지않음
     *
     * @param -- ranking 베스트셀러 랭킹 --
     * @param -- link 책으로 들어가는 링크 --
     *
     * @return -- BookDTO 베스트셀러 정보가 담긴 BookDTO --
     * @author -- 정재익 --
     * @since -- 3월 04일 --
     */
    private fun getBestSellerBookDTO(ranking: Int, link: String, maxRetries: Int = 3): BookDTO {
        var retries = 0
        val doc: Document = Jsoup.connect(link).get()

        while (true) {
            val title = doc.selectFirst("h2.gd_name")?.text()?.takeIf { it.isNotBlank() }  ?: "제목 정보 없음"
            val author = doc.selectFirst("span.gd_auth")?.text()?.takeIf { it.isNotBlank() }  ?: "작가 정보 없음"
            val description = Jsoup.clean(doc.selectFirst("div#infoset_introduce div.infoWrap_txtInner")?.text()?.takeIf { it.isNotBlank() }  ?: "설명 없음", Safelist.none()).trim()
            val image = doc.selectFirst("img.gImg")?.attr("src")?.takeIf { it.isNotBlank() }  ?: "이미지 없음"
            val isbn = extractIsbn(doc)

            if (title == "제목 정보 없음" || author == "작가 정보 없음" || description == "설명 없음" ||
                image == "이미지 없음" || isbn == ""
            ) {
                retries++
                if (retries >= maxRetries) {
                    return BookDTO(null, title, author, description, image, isbn, ranking, 0)
                }
                continue
            }
            return BookDTO(null, title, author, description, image, isbn, ranking, 0)
        }
    }

    /**
     * -- 크롤링 중 ISBN13 추출 메서드 --
     *
     * @param -- doc html문서 --
     * @param -- default isbn이 없을 경우 기본값 반환 --
     *
     * @return -- isbn 문자열 --
     * @author -- 정재익 --
     * @since -- 3월 04일 --
     */
    private fun extractIsbn(doc: Document, default: String = "ISBN 정보 없음"): String {
        val tableScope = doc.selectFirst("div.infoSetCont_wrap .b_size")
        val isbn = tableScope?.selectFirst("tr:has(th:contains(ISBN13)) td")?.text() ?: default
        return isbn
    }
}