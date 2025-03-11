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
    private static final String WEEKLY_BOOKS_RANKING_KEY = "weekly_books_ranking";

    public void updateWeeklyRanking(String rankingKey, List<Object[]> favoriteCounts, List<Object[]> reviewCounts) {
        Map<Long, Integer> favoriteMap = favoriteCounts.stream()
                .collect(Collectors.toMap(data -> (Long) data[0], data -> ((Long) data[1]).intValue()));
        Map<Long, Integer> reviewMap = reviewCounts.stream()
                .collect(Collectors.toMap(data -> (Long) data[0], data -> ((Long) data[1]).intValue()));

        Set<Long> allIds = new HashSet<>();
        allIds.addAll(favoriteMap.keySet());
        allIds.addAll(reviewMap.keySet());

        for (Long itemId : allIds) {
            int favoriteCount = favoriteMap.getOrDefault(itemId, 0);
            int reviewCount = reviewMap.getOrDefault(itemId, 0);
            double score = (favoriteCount * 0.5) + (reviewCount * 0.5);

            redisTemplate.opsForZSet().add(rankingKey, String.valueOf(itemId), score);
        }
    }

    public List<Map<String, Object>> getWeeklyRanking(String rankingKey) {
        Set<ZSetOperations.TypedTuple<Object>> rankings = redisTemplate.opsForZSet()
                .reverseRangeWithScores(rankingKey, 0, 9);

        List<Map<String, Object>> rankingList = new ArrayList<>();
        int rank = 1;
        int displayRank = 1;
        Double prevScore = null;

        for (ZSetOperations.TypedTuple<Object> entry : rankings) {
            Map<String, Object> ranking = new HashMap<>();
            double score = entry.getScore();
            Object itemId = entry.getValue();

            if (prevScore != null && !prevScore.equals(score)) {
                rank = displayRank;
            }

            ranking.put("rank", rank);
            ranking.put("item_id", itemId);
            rankingList.add(ranking);

            prevScore = score;
            displayRank++;
        }

        return rankingList;
    }

    public void updateWeeklyBooksRanking(LocalDateTime start, LocalDateTime end) {
        List<Object[]> favoriteCounts = favoriteRepository.findFavoriteCounts(start, end);
        List<Object[]> reviewCounts = reviewRepository.findReviewCounts(start, end);

        updateWeeklyRanking(WEEKLY_BOOKS_RANKING_KEY, favoriteCounts, reviewCounts);
    }

    public List<Map<String, Object>> getWeeklyBookRanking() {
        return getWeeklyRanking(WEEKLY_BOOKS_RANKING_KEY);
    }
}
