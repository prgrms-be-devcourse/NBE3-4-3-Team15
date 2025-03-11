package com.project.backend.domain.ranking.service;

import com.project.backend.domain.book.exception.BookErrorCode;
import com.project.backend.domain.book.exception.BookException;
import com.project.backend.domain.book.repository.FavoriteRepository;
import com.project.backend.domain.ranking.common.RankingType;
import com.project.backend.domain.ranking.exception.RankingErrorCode;
import com.project.backend.domain.ranking.exception.RankingException;
import com.project.backend.domain.review.comment.repository.ReviewCommentRepository;
import com.project.backend.domain.review.exception.ReviewErrorCode;
import com.project.backend.domain.review.exception.ReviewException;
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

    public void updateRanking(RankingType rankingType, LocalDateTime start, LocalDateTime end) {
        List<Object[]> firstCounts;
        List<Object[]> secondCounts;
        double weight1, weight2;

        switch (rankingType) {
            case WEEKLY_BOOKS:
                firstCounts = favoriteRepository.findFavoriteCounts(start, end);
                secondCounts = reviewRepository.findReviewCounts(start, end);
                weight1 = 0.5;
                weight2 = 0.5;
                if (firstCounts.isEmpty()) { throw new BookException(BookErrorCode.NO_FAVORITE_BOOKS); }
                if (secondCounts.isEmpty()) throw createReviewException(ReviewErrorCode.REVIEW_NOT_FOUND);
                break;

            case WEEKLY_REVIEWS:
            case DAILY_REVIEWS:
                firstCounts = reviewRecommendationRepository.findReviewRecommendCounts(start, end);
                secondCounts = reviewCommentRepository.findReviewCommentCounts(start, end);
                weight1 = (rankingType == RankingType.WEEKLY_REVIEWS) ? 0.7 : 0.6;
                weight2 = (rankingType == RankingType.WEEKLY_REVIEWS) ? 0.3 : 0.4;
                if (firstCounts.isEmpty()) throw createReviewException(ReviewErrorCode.REVIEW_RECOMMENDATION_NOT_FOUND);
                if (secondCounts.isEmpty()) throw createReviewException(ReviewErrorCode.REVIEW_COMMENT_NOT_FOUND);
                break;

            default:
                throw new RankingException(RankingErrorCode.UNKNOWN_RANKING_TYPE);
        }

        updateRankingInRedis(rankingType.getKey(), firstCounts, secondCounts, weight1, weight2);
    }

    private ReviewException createReviewException(ReviewErrorCode errorCode) {
        return new ReviewException(errorCode.getStatus(), errorCode.getErrorCode(), errorCode.getMessage());
    }

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
}
