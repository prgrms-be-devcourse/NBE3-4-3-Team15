package com.project.backend.domain.book.crawling

import com.project.backend.domain.book.repository.RedisRepository
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

/**
 * -- Facade 클래스 --
 * Facade 패턴을 이용하여 크롤링 로직을 단순화 하기 위한 클래스
 *
 * @author -- 정재익 --
 * @since -- 3월 09일 --
 */
@Component
class CrawlingFacade(private val crawlingService2: CrawlingService2, private val redisRepository: RedisRepository) {


    /**
     * -- 크롤링 실행 및 통제 메서드 --
     *
     * 각각의 책으로 들어가는 순위와 url이 담긴 베스트셀러 Map 크롤링
     * Redis에 저장한 이전 해시값과 현재 해시값을 비교해 베스트셀러가 바뀌었는지 검사
     * 바뀌었으면 상세페이지 크롤링 실행
     * 바뀌지 않았으면 크롤링 생략
     *
     * @author -- 정재익 --
     * @since -- 3월 09일 --
     */
    fun executeCrawling() { runBlocking {
            crawlingService2.getBestSellersMap()
            val currentHash = crawlingService2.getMapHash()
            val previousHash = redisRepository.loadPreviousHash()

//            if (currentHash != previousHash) {
                val bestSellerBookDTOs = crawlingService2.getBestSellerBookDTOs()
                crawlingService2.saveBestSellers(bestSellerBookDTOs)
                redisRepository.saveHash(currentHash)
//            } else {
//                crawlingService2.logNoChange()
//            }
        }
    }
}