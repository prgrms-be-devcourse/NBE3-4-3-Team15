package com.project.backend.domain.book.crawling

import com.project.backend.domain.book.dto.BookDTO
import com.project.backend.domain.book.service.BookService
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
class CrawlingService(private val bookService: BookService) {
    private val logger = LoggerFactory.getLogger(CrawlingService::class.java)
    private lateinit var bestSellersMap: Map<Int, String>

    /**
     * -- url에 접속하여 베스트셀러의 순위와 url을 가져오는 메소드 --
     * bestSellersMap에 베스트셀러의 순위와 url을 저장함
     *
     * @author -- 정재익 --
     * @since -- 3월 09일 --
     */
    fun getBestSellersMap() {
        val targetUrl = "https://www.yes24.com/Product/Category/RealTimeBestSeller?categoryNumber=001"
        val baseUrl = "https://www.yes24.com"

        val bestSellerLinks = Jsoup.connect(targetUrl).get()
            .select("#yesBestList > li > div > div.item_info > div.info_row.info_name > a.gd_name").eachAttr("href")
        extractThread("베스트셀러 목록 순위 가져오기")

        bestSellersMap = bestSellerLinks.mapIndexed { index, href -> (index + 1) to "$baseUrl$href" }.toMap()
    }

    /**
     * -- Map을 해싱하는 메서드 --
     * bestSellersMap를 이용하여 해시값 추출
     *
\    * @return -- String 해시 값--
     * @author -- 정재익 --
     * @since -- 3월 09일 --
     */
     fun getMapHash(): String {
        val mapString = bestSellersMap.toSortedMap().entries.joinToString { "${it.key}:${it.value}" }
        extractThread("맵 해싱")

        return MessageDigest.getInstance("SHA-256")
            .digest(mapString.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    /**
     * -- 상세페이지 크롤링 코루틴 설정 메서드 --*
     * Dispatchers.IO에 5개의 스레드 제한을 지정 후 상세페이지 크롤링 실행
     * bestSellersMap에 담긴 url로 접속하여 세부페이지 크롤링 실행
     * 모든 작업이 끝나고 awaitAll로 BookDTO를 합쳐 리스트로 만들어 반환
     *
     * @return -- List<BookDTO> 베스트셀러 정보가 입력된 책 리스트 --
     * @author -- 정재익 --
     * @since -- 3월 09일 --
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getBestSellerBookDTOs(): List<BookDTO> = withContext(Dispatchers.IO.limitedParallelism(5)){
        bestSellersMap.map { (ranking, link) ->
            async { getBestSellerBookDTO(ranking, link)}
        }.awaitAll()
    }

    /**
     * -- 상세페이지 크롤링 메시드 --
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
        extractThread("${ranking}위 크롤링")

        while (true) {
            val title = doc.selectFirst("h2.gd_name")?.text()?.takeIf { it.isNotBlank() } ?: "제목 정보 없음"
            val author = doc.selectFirst("span.gd_auth")?.text()?.takeIf { it.isNotBlank() } ?: "작가 정보 없음"
            val description = Jsoup.clean(doc.selectFirst("div#infoset_introduce div.infoWrap_txtInner")?.text()?.takeIf { it.isNotBlank() } ?: "설명 없음", Safelist.none()).trim()
            val image = doc.selectFirst("img.gImg")?.attr("src")?.takeIf { it.isNotBlank() } ?: "이미지 없음"
            val isbn = extractIsbn(doc)

            if (title == "제목 정보 없음" || author == "작가 정보 없음" || description == "설명 없음" || image == "이미지 없음" || isbn == "") {
                retries++
                if (retries >= maxRetries) { return BookDTO(null, title, author, description, image, isbn, ranking, 0) }
                continue
            }
            return BookDTO(null, title, author, description, image, isbn, ranking, 0)
        }
    }

    /**
     * -- 크롤링 중 ISBN13 추출 메서드 --
     *
     * @param -- doc 상세페이지 html --
     * @return -- 13자리 isbn --
     *
     * @author -- 정재익 --
     * @since -- 3월 04일 --
     */
    private fun extractIsbn(doc: Document, default: String = "ISBN 정보 없음"): String {
        val tableScope = doc.selectFirst("div.infoSetCont_wrap .b_size")
        return tableScope?.selectFirst("tr:has(th:contains(ISBN13)) td")?.text() ?: default
    }

    /**
     * -- 베스트셀러 저장 메소드 --
     *
     * @param -- List<BookDTO> 베스트셀러 --
     *
     * @author -- 정재익 --
     * @since -- 3월 09일 --
     */
    fun saveBestSellers(bestSellerBookDTOs: List<BookDTO>) {
        extractThread("베스트셀러 저장")
        bookService.saveBestsellers(bestSellerBookDTOs)
    }

    /**
     * -- 베스트셀러 변동 없을때 로그 메소드 --
     *
     * @author -- 정재익 --
     * @since -- 3월 09일 --
     */
    fun logNoChange() {
        logger.info("베스트셀러 변경 없음. 크롤링 생략")
    }

    /**
     * -- 스레드 관리 로그 메소드 --
     *
     * @author -- 정재익 --
     * @since -- 3월 09일 --
     */
    private fun extractThread(str: Any) {
        logger.info("${Thread.currentThread().name} ${str} 실행 ${java.time.LocalDateTime.now()}")
    }
}