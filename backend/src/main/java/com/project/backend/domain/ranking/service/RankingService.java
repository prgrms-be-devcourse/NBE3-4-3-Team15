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
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String WEEKLY_RANKING_KEY = "weekly_book_ranking";

    public void updateWeeklyBooksRanking(LocalDateTime start, LocalDateTime end) {
        List<Object[]> favoriteCounts = favoriteRepository.findFavoriteCounts(start, end);
        List<Object[]> reviewCounts = reviewRepository.findReviewCounts(start, end);

        Map<Long, Integer> favoriteMap = favoriteCounts.stream()
                .collect(Collectors.toMap(data -> (Long) data[0], data -> ((Long) data[1]).intValue()));
        Map<Long, Integer> reviewMap = reviewCounts.stream()
                .collect(Collectors.toMap(data -> (Long) data[0], data -> ((Long) data[1]).intValue()));

        Set<Long> allBookIds = new HashSet<>();
        allBookIds.addAll(favoriteMap.keySet());
        allBookIds.addAll(reviewMap.keySet());

        for (Long bookId : allBookIds) {
            int favoriteCount = favoriteMap.getOrDefault(bookId, 0);
            int reviewCount = reviewMap.getOrDefault(bookId, 0);
            double score = (favoriteCount * 0.5) + (reviewCount * 0.5);

            redisTemplate.opsForZSet().add(WEEKLY_RANKING_KEY, String.valueOf(bookId), score);
        }
    }

    public List<Map<String, Object>>  getWeeklyRanking() {
        Set<ZSetOperations.TypedTuple<Object>> rankings = redisTemplate.opsForZSet()
                .reverseRangeWithScores(WEEKLY_RANKING_KEY, 0, 9);

        List<Map<String, Object>> rankingList = new ArrayList<>();
        int rank = 1;
        for (Object bookId : rankings) {
            Map<String, Object> ranking = new HashMap<>();
            ranking.put("rank", rank++);
            ranking.put("book_id", bookId);
            rankingList.add(ranking);
        }

        return rankingList;
    }
}
