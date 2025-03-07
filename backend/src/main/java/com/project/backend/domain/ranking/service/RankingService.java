package com.project.backend.domain.ranking.service;

import com.project.backend.domain.book.repository.FavoriteRepository;
import com.project.backend.domain.ranking.entity.Ranking;
import com.project.backend.domain.ranking.repository.RankingRepository;
import com.project.backend.domain.review.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * -- 랭킹 서비스 --
 *
 * @author -- 김남우 --
 * @since -- 3월 4일 --
 */
@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;

    // 주간 인기 도서 TOP 10 가져오기
    public List<Ranking> getWeeklyTopBooks() {
        return rankingRepository.findTop10ByTypeOrderByScoreDesc("주간/인기");
    }

    public void updateWeeklyBooksRanking(LocalDateTime start, LocalDateTime end) {
        // favorite 테이블에서 찜 개수 계산
        List<Object[]> favoriteCounts = favoriteRepository.countFavoritesByBookIdAndDateTime(start, end);

        // review 테이블에서 리뷰 개수 계산
        List<Object[]> reviewCounts = reviewRepository.countReviewsByBookIdAndDateTime(start, end);

        // 각 책에 대해 점수 계산
        Map<Long, Double> bookScores = new HashMap<>();

        for (Object[] favorite : favoriteCounts) {
            Long bookId = (Long) favorite[0];
            Long favoriteCount = (Long) favorite[1];
            bookScores.put(bookId, bookScores.getOrDefault(bookId, 0.0) + favoriteCount * 0.5);
        }

        for (Object[] review : reviewCounts) {
            Long bookId = (Long) review[0];
            Long reviewCount = (Long) review[1];
            bookScores.put(bookId, bookScores.getOrDefault(bookId, 0.0) + reviewCount * 0.5);
        }

        // 랭킹을 계산하여 Ranking 엔티티에 저장
        List<Ranking> rankings = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : bookScores.entrySet()) {
            Ranking ranking = new Ranking();
            ranking.setType("주간/인기");
            ranking.setItem(entry.getKey());
            ranking.setScore(entry.getValue());
            ranking.setUpdatedAt(LocalDateTime.now());
            rankings.add(ranking);
        }

        // 랭킹을 저장
        rankingRepository.saveAll(rankings);

        // TOP 10 순위 가져오기
        List<Ranking> top10Books = rankingRepository.findTop10ByTypeOrderByScoreDesc("주간/인기");
        System.out.println("주간 인기 도서 TOP 10: " + top10Books);
    }

//    private double calculateRankingScore(Long favoriteCount, Long reviewCount){
//        double score = (favoriteCount * 0.5) + (reviewCount * 0.5);
//        return score;
//    }

}
