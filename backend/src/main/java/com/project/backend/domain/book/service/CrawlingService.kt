package com.project.backend.domain.book.service

import com.project.backend.domain.book.dto.BookDTO
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Safelist
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

/**
 * -- 크롤링 서비스 --
 *
 * @author -- 정재익 --
 * @since -- 3월 01일 --
 */
@Service
class CrawlingService(private val bookService: BookService, private val redisHash: RedisHash) {


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
        val previousHash = redisHash.loadPreviousHash()
        println("Current hash: $currentHash")
        println("Previous hash: $previousHash")

        if (previousHash != currentHash) {
            printWithThread("크롤링 시작")
            val bestSellerBookDTOs = getBestSellerBookDTOs(bestSellerMaps)
            bookService.saveBestsellers(bestSellerBookDTOs)
            redisHash.saveHash(currentHash)
            printWithThread("크롤링 완료")
        } else {
            printWithThread("베스트셀러 변경 없음, 크롤링 생략")
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
        }.toMap().also {
            printWithThread("${bestSellerLinks.size}개 링크 크롤링 완료")
        }
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
     * @param -- ranking 베스트셀러 랭킹 --
     * @param -- link 책으로 들어가는 링크 --
     *
     * @return -- BookDTO 베스트셀러 정보가 담긴 BookDTO --
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    private fun getBestSellerBookDTO(ranking: Int, link: String): BookDTO {
        val doc: Document = Jsoup.connect(link).get()

        val title = doc.selectFirst("#yDetailTopWrap > div.topColRgt > div.gd_infoTop > div > h2")?.text() ?: "제목 없음"
        val author =
            doc.selectFirst("#yDetailTopWrap > div.topColRgt > div.gd_infoTop > span.gd_pubArea > span.gd_auth > a:nth-child(1)")
                ?.text() ?: "저자 없음"
        val image =
            doc.selectFirst("#yDetailTopWrap > div.topColLft > div > div.gd_3dGrp.gdImgLoadOn > div > span.gd_img > em > img")
                ?.attr("src") ?: "이미지 없음"
        val isbn =
            doc.selectFirst("#infoset_specific > div.infoSetCont_wrap > div > table > tbody > tr:nth-child(3) > td")
                ?.text() ?: "ISBN 없음"
        val rawDescription =
            doc.selectFirst("#infoset_introduce > div.infoSetCont_wrap > div.infoWrap_txt")?.text() ?: "설명 없음"
        val description = Jsoup.clean(rawDescription, Safelist.none()).trim()

        return BookDTO(
            id = null,
            title = title,
            author = author,
            description = description,
            image = image,
            isbn = isbn,
            ranking = ranking,
            favoriteCount = 0
        ).also {
            printWithThread("${ranking}위 책 크롤링 완료")
        }
    }

    /**
     * -- 크롤링 시간측정을 위한 메소드 --
     *
     * @param -- str 모든타입 설정 가능-
     *
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    private fun printWithThread(str: Any) {
        val time = System.currentTimeMillis()
        val formattedTime = SimpleDateFormat("mm분 ss초 SSS", Locale.getDefault()).format(Date(time))
        println("$str $formattedTime")
    }
}