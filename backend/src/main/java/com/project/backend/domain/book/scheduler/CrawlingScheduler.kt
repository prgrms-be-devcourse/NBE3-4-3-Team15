package com.project.backend.domain.book.scheduler

import com.project.backend.domain.book.service.CrawlingService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * -- 크롤링 스케줄러 --
 *
 * @author -- 정재익 --
 * @since -- 3월 01일 --
 */
@Component
class CrawlingScheduler(private val crawlingService: CrawlingService) {
    private val logger = LoggerFactory.getLogger(CrawlingScheduler::class.java)

    /**
     * -- 크롤링 스케줄러 --
     * 매시간 10분마다 베스트셀러 크롤링
     * yes24에서 매시간 0분마다 베스트셀러 갱신
     *
     * @author -- 정재익 --
     * @since -- 3월 01일 --
     */
    @Scheduled(cron = "0 10 * * * *")
    fun scheduledCrawling() {
        logger.info("크롤링 시작: ${java.time.LocalDateTime.now()}")
        crawlingService.main()
        logger.info("크롤링 완료: ${java.time.LocalDateTime.now()}")
    }
}
