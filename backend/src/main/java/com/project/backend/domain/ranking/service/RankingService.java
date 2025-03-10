package com.project.backend.domain.ranking.service;

import com.project.backend.domain.book.repository.FavoriteRepository;
import com.project.backend.domain.review.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * -- 랭킹 서비스 --
 *
 * @author -- 김남우 --
 * @since -- 3월 4일 --
 */
@Service
@RequiredArgsConstructor
public class RankingService {

    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;

    private static final String WEEKLY_RANKING_KEY = "weekly_book_ranking";
    private final RedisTemplate<String, Object> redisTemplate;

    public void updateWeeklyBooksRanking(LocalDateTime start, LocalDateTime end) {
        List<Object[]> favoriteCounts = favoriteRepository.findFavoriteCounts(start, end);
        System.out.println("Favorite : " + favoriteCounts);
        List<Object[]> reviewCounts = reviewRepository.findReviewCounts(start, end);
        System.out.println("Review : " + reviewCounts);

        Map<Long, Integer> favoriteMap = favoriteCounts.stream()
                .collect(Collectors.toMap(data -> (Long) data[0], data -> ((Long) data[1]).intValue()));
        Map<Long, Integer> reviewMap = reviewCounts.stream()
                .collect(Collectors.toMap(data -> (Long) data[0], data -> ((Long) data[1]).intValue()));

        for (Long bookId : favoriteMap.keySet()) {
            int favoriteCount = favoriteMap.getOrDefault(bookId, 0);
            int reviewCount = reviewMap.getOrDefault(bookId, 0);
            double score = (favoriteCount * 0.5) + (reviewCount * 0.5);

            redisTemplate.opsForZSet().add(WEEKLY_RANKING_KEY, String.valueOf(bookId), score);
        }
    }

//    private double calculateRankingScore(Long favoriteCount, Long reviewCount){
//        double score = (favoriteCount * 0.5) + (reviewCount * 0.5);
//        return score;
//    }

}
