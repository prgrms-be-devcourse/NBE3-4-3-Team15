package com.project.backend.domain.book.service
//
//import com.example.demo.dto.BookDTO
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import com.microsoft.playwright.Browser
//import com.microsoft.playwright.BrowserType
//import com.microsoft.playwright.Page
//import com.microsoft.playwright.Playwright
//import com.microsoft.playwright.options.LoadState
//import com.microsoft.playwright.options.WaitUntilState
//import jakarta.transaction.Transactional
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import org.springframework.stereotype.Service
//import java.text.SimpleDateFormat
//import java.util.*
//import java.util.concurrent.CountDownLatch
//import kotlin.concurrent.thread
//
///**
// * -- PlayWright 크롤링 클래스 --
// * 현재는 사용안함 예비용임 교보문고를 크롤링함
// *
// * @author -- 정재익 --
// * @since -- 3월 1일 --
// */
//@Service
//class PlayWrightCrawlingService(private val bookService: BookService) {
//
//    private val latch = CountDownLatch(2)
//    private val completionLatch = CountDownLatch(4)
//    private var bestSellers: MutableList<BookDTO> = Collections.synchronizedList(mutableListOf())
//    private var bookLinks: MutableList<String> = mutableListOf()
//
///**
// * -- 베스트셀러 크롤링 메소드 --
// * 스레드를 4개 배정한다
// *
// * @author -- 정재익 --
// * @since -- 3월 01일 --
// */
//    @Transactional
//    fun main() {
//        printWithThread("크롤링 시작", 0)
//        repeat(4) { threadIndex ->
//            thread { startCrawling(threadIndex) }
//        }
//    }
//
///**
// * -- 베스트셀러 크롤링 메소드 --
// * 각각의 스레드에 playWright의 인스턴스를 담는다
// * 비동기 처리를 시작하여 크롤링 범위를 덮어쓴다
// * 크롤링한 데이터를 DB에 저장한다
// *
// * @param -- threadIndex 스레드 번호 --
// * @author -- 정재익 --
// * @since -- 3월 01일 --
// */
//    fun startCrawling(threadIndex: Int) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val playwright = Playwright.create()
//            val browser = playwright.chromium().launch(BrowserType.LaunchOptions().setHeadless(true))
//            bestSellers = scrapeBookData(browser, bookLinks, threadIndex).toMutableList()
//            getBookLinks(browser, threadIndex)
//
//            completionLatch.countDown()
//
//            browser.close()
//            playwright.close()
//        }
//
//            if (threadIndex == 0) {
//                completionLatch.await()
//                printWithThread("📌 bestSellers 크기: ${bestSellers.size}", threadIndex)
//                printWithThread("데이터 저장 시작", threadIndex)
//                bookService.saveBestsellers(bestSellers)
//                printWithThread("데이터 저장 완료", threadIndex)
//                printWithThread("크롤링 완료", threadIndex)
//
//            }
//
//    }
//
///**
// * -- 베스트셀러 링크를 가져오는 메소드 --
// * 1번 스레드와 2번 스레드만 각각 1페이지 2페이지의 베스트셀러 총 100권 크롤링
// * 전역변수로 지정된 bookLinks에 링크 저장
// *
// * @param -- browser PlayWright 브라우저--
// * @param -- threadIndex 스레드 번호 --
// *
// * @author -- 정재익 --
// * @since -- 3월 01일 --
// */
//    private fun getBookLinks(browser: Browser, threadIndex: Int) {
//        val page = browser.newPage()
//
//        if (threadIndex == 1 || threadIndex == 2) {
//
//            page.navigate("https://store.kyobobook.co.kr/bestseller/realtime?page=$threadIndex&per=50")
//            page.waitForLoadState(LoadState.NETWORKIDLE)
//            page.waitForSelector("div.ml-4 > .prod_link")
//
//            val links = page.locator("div.ml-4 > .prod_link").all()
//            bookLinks.addAll(links.mapNotNull { it.getAttribute("href") })
//            printWithThread("${threadIndex}페이지 ${bookLinks.size}개의 도서 링크 수집 완료", threadIndex)
//
//            latch.countDown()
//        }
//        page.close()
//        latch.await()
//    }
///**
// * -- 베스트셀러 정보를 가져오고 BookDTO에 입력하는 메서드 --
// *
// * @param -- browser PlayWright 브라우저--
// * @param -- bookLinks 링크 리스트 --
// * @param -- threadIndex 스레드 번호 --
// *
// * @return -- List<BookDTO> 베스트셀러 정보가 담긴 리스트 --
// * @author -- 정재익 --
// * @since -- 3월 01일 --
// */
//    private suspend fun scrapeBookData(browser: Browser, bookLinks: List<String>, threadIndex: Int): List<BookDTO> {
//
//        bookLinks.forEachIndexed { ranking, bookLink ->
//            if (ranking % 4 == threadIndex) {
//                val page = browser.newPage()
//                printWithThread("${ranking}, ${bookLink} 접근 시작", threadIndex)
//                page.navigate(bookLink, Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT))
//                printWithThread("${ranking}, ${bookLink} 접근 완료", threadIndex)
//
//                val data = page.evaluate(
//                    """ () => JSON.stringify({
//                            title: document.querySelector('.prod_title')?.innerText?.trim() || '',
//                            author: document.querySelector('.author')?.innerText?.trim() || '',
//                            isbn: document.querySelector('#scrollSpyProdInfo .product_detail_area.basic_info table tbody tr:nth-child(1) td')?.innerText?.trim() || '',
//                            description: document.querySelector('.intro_bottom')?.innerText?.trim() || '',
//                            image: document.querySelector('.portrait_img_box img')?.getAttribute('src') || ''
//                        }) """
//                ).toString()
//
//                val type = object : TypeToken<Map<String, String>>() {}.type
//                val json: Map<String, String> = Gson().fromJson(data, type)
//
//                printWithThread("${ranking} 데이터 파싱 완료", threadIndex)
//
//                if (json.values.any { it.isNotBlank() }) {
//                    bestSellers.add(
//                        BookDTO(
//                            id = 0L,
//                            title = json["title"] ?: "",
//                            author = json["author"] ?: "",
//                            description = json["description"] ?: "",
//                            image = json["image"] ?: "",
//                            isbn = json["isbn"] ?: "",
//                            ranking = ranking + 1,
//                            favoriteCount = 0
//                        )
//                    )
//                }
//                page.close()
//            }
//        }
//        return bestSellers
//    }
//
///**
// * -- 크롤링 시간측정을 위한 메소드 --
// *
// * @param -- str 모든타입 설정 가능-
// * @param -- threadIndex 스레드 번호 --
// *
// * @author -- 정재익 --
// * @since -- 3월 01일 --
// */
//    private fun printWithThread(str: Any, threadIndex: Int) {
//        val time = System.currentTimeMillis()
//        val formattedTime = SimpleDateFormat("mm분 ss초 SSS", Locale.getDefault()).format(Date(time))
//
//        println("Thread[$threadIndex] $str $formattedTime")
//    }
//}
