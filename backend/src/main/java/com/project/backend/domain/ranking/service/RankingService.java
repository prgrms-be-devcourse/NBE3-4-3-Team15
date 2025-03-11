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

    public List<Map<String, Object>> getWeeklyBookRanking() {
        Set<ZSetOperations.TypedTuple<Object>> rankings = redisTemplate.opsForZSet()
                .reverseRangeWithScores(WEEKLY_RANKING_KEY, 0, 9);

        List<Map<String, Object>> rankingList = new ArrayList<>();

        int rank = 1;
        int displayRank = 1;
        Double prevScore = null;

        for (ZSetOperations.TypedTuple<Object> entry : rankings) {
            Map<String, Object> ranking = new HashMap<>();
            double score = entry.getScore();
            Object bookId = entry.getValue();

            // 이전 점수와 다르면 표시 등수를 현재 등수로 업데이트
            if (prevScore != null && !prevScore.equals(score)) {
                rank = displayRank;
            }

            ranking.put("rank", rank);
            ranking.put("item_id", bookId);
//            ranking.put("score", score);
            rankingList.add(ranking);

            prevScore = score;
            displayRank++;
        }

        return rankingList;
    }
}
