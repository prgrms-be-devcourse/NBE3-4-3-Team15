package com.project.backend.domain.ranking.service;

import com.project.backend.domain.book.repository.FavoriteRepository;
import com.project.backend.domain.ranking.common.RankingType;
import com.project.backend.domain.ranking.exception.RankingErrorCode;
import com.project.backend.domain.ranking.exception.RankingException;
import com.project.backend.domain.review.comment.repository.ReviewCommentRepository;
import com.project.backend.domain.review.recommendation.repository.ReviewRecommendationRepository;
import com.project.backend.domain.review.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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
    private final ReviewRecommendationRepository reviewRecommendationRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String WEEKLY_BOOKS_RANKING_KEY = "weekly_books_ranking";
    private static final String WEEKLY_REVIEWS_RANKING_KEY = "weekly_reviews_ranking";
    private static final String DAILY_REVIEWS_RANKING_KEY = "daily_books_ranking";





    private void updateRankingInRedis(String rankingKey, List<Object[]> counts1, List<Object[]> counts2, double weight1, double weight2) {
        Map<Long, Double> scores = new HashMap<>();

        counts1.forEach(data -> scores.merge((Long) data[0], ((Long) data[1]) * weight1, Double::sum));
        counts2.forEach(data -> scores.merge((Long) data[0], ((Long) data[1]) * weight2, Double::sum));

        scores.forEach((itemId, score) ->
                redisTemplate.opsForZSet().add(rankingKey, String.valueOf(itemId), score)
        );
    }

    public List<Map<String, Object>> getRanking(RankingType rankingType) {
        Set<ZSetOperations.TypedTuple<Object>> rankings = redisTemplate.opsForZSet()
                .reverseRangeWithScores(rankingType.getKey(), 0, 9);

        List<Map<String, Object>> rankingList = new ArrayList<>();
        int rank = 1;
        Double prevScore = null;

        for (int i = 0; i < rankings.size(); i++) {
            ZSetOperations.TypedTuple<Object> entry = (ZSetOperations.TypedTuple<Object>) rankings.toArray()[i];

            Map<String, Object> ranking = new HashMap<>();
            double score = entry.getScore();
            Object itemId = entry.getValue();

            if (prevScore != null && !prevScore.equals(score)) {
                rank = i + 1;
            }

            ranking.put("rank", rank);
            ranking.put("item_id", itemId);
            ranking.put("score", score);
            rankingList.add(ranking);

            prevScore = score;
        }

        return rankingList;
    }

    public void updateWeeklyBooksRanking(LocalDateTime start, LocalDateTime end) {
        List<Object[]> favoriteCounts = favoriteRepository.findFavoriteCounts(start, end);
        List<Object[]> reviewCounts = reviewRepository.findReviewCounts(start, end);

        updateRanking(WEEKLY_BOOKS_RANKING_KEY, favoriteCounts, reviewCounts, 0.5, 0.5);
    }

    public void updateWeeklyReviewsRanking(LocalDateTime start, LocalDateTime end) {
        List<Object[]> recommendCounts = reviewRecommendationRepository.findReviewRecommendCounts(start, end);
        List<Object[]> CommentCounts = reviewCommentRepository.findReviewCommentCounts(start, end);

        updateRanking(WEEKLY_REVIEWS_RANKING_KEY, recommendCounts, CommentCounts, 0.7, 0.3);
    }

    public void updateDailyReviewsRanking(LocalDateTime start, LocalDateTime end) {
        List<Object[]> recommendCounts = reviewRecommendationRepository.findReviewRecommendCounts(start, end);
        List<Object[]> commentCounts = reviewCommentRepository.findReviewCommentCounts(start, end);

        updateRanking(DAILY_REVIEWS_RANKING_KEY, recommendCounts, commentCounts, 0.6, 0.4);
    }

    public List<Map<String, Object>> getWeeklyBookRanking() {
        return getRanking(WEEKLY_BOOKS_RANKING_KEY);
    }

    public List<Map<String, Object>> getWeeklyReviewRanking() {
        return getRanking(WEEKLY_REVIEWS_RANKING_KEY);
    }

    public List<Map<String, Object>> getDailyReviewsRanking() {
        return getRanking(DAILY_REVIEWS_RANKING_KEY);
    }
}
