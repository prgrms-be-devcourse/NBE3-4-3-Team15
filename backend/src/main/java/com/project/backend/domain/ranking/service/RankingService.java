package com.project.backend.domain.ranking.service;

import com.project.backend.domain.book.repository.BookRepository;
import com.project.backend.domain.book.repository.FavoriteRepository;
import com.project.backend.domain.ranking.common.RankingType;
import com.project.backend.domain.ranking.dto.RankingDTO;
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
 * @since -- 2025.03.04 --
 */
@Service
@RequiredArgsConstructor
public class RankingService {

    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewRecommendationRepository reviewRecommendationRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final BookRepository bookRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 특정 기간 동안의 데이터를 기반으로 랭킹을 업데이트하는 메서드
     *
     * @param rankingType 랭킹 유형
     * @param start 랭킹 집계 시작 시간
     * @param end 랭킹 집계 종료 시간
     *
     * @author 김남우
     * @since 2025.03.09
     */
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
                break;

            case WEEKLY_REVIEWS:
            case DAILY_REVIEWS:
                firstCounts = reviewRecommendationRepository.findReviewRecommendCounts(start, end);
                secondCounts = reviewCommentRepository.findReviewCommentCounts(start, end);
                weight1 = (rankingType == RankingType.WEEKLY_REVIEWS) ? 0.7 : 0.6;
                weight2 = (rankingType == RankingType.WEEKLY_REVIEWS) ? 0.3 : 0.4;
                break;

            default:
                throw new RankingException(RankingErrorCode.UNKNOWN_RANKING_TYPE);
        }

        updateRankingInRedis(rankingType.getKey(), firstCounts, secondCounts, weight1, weight2);
    }


    /**
     * Redis에 랭킹 데이터를 업데이트하는 메서드
     *
     * @param rankingKey Redis에 저장될 키 값
     * @param counts1 첫 번째 기준 데이터 리스트
     * @param counts2 두 번째 기준 데이터 리스트
     * @param weight1 첫 번째 기준 가중치
     * @param weight2 두 번째 기준 가중치
     *
     * @author 김남우
     * @since 2025.03.11
     */
    private void updateRankingInRedis(String rankingKey, List<Object[]> counts1, List<Object[]> counts2, double weight1, double weight2) {
        redisTemplate.delete(rankingKey);

        Map<Long, Double> scores = new HashMap<>();

        counts1 = counts1 != null ? counts1 : new ArrayList<>();
        counts2 = counts2 != null ? counts2 : new ArrayList<>();

        counts1.forEach(data -> scores.merge((Long) data[0], ((Long) data[1]) * weight1, Double::sum));
        counts2.forEach(data -> scores.merge((Long) data[0], ((Long) data[1]) * weight2, Double::sum));

        scores.forEach((itemId, score) ->
                redisTemplate.opsForZSet().add(rankingKey, String.valueOf(itemId), score)
        );
    }

    /**
     * Redis에서 랭킹 데이터를 조회하는 메서드
     *
     * @param rankingType 조회할 랭킹 유형
     * @return 랭킹 리스트
     *
     * @author 김남우
     * @since 2025.03.09
     */
    public List<RankingDTO> getRanking(RankingType rankingType) {
        int maxRank = rankingType == RankingType.DAILY_REVIEWS ? 5 : 10;
        Set<ZSetOperations.TypedTuple<Object>> rankings = redisTemplate.opsForZSet()
                .reverseRangeWithScores(rankingType.getKey(), 0, maxRank - 1);

        List<RankingDTO> rankingList = new ArrayList<>();
        int rank = 1;
        Double prevScore = null;
        int index = 0;

        for (ZSetOperations.TypedTuple<Object> entry : rankings) {
            double score = entry.getScore();
            Long itemId = Long.valueOf(entry.getValue().toString());

            if (prevScore != null && !prevScore.equals(score)) {
                rank = index + 1;
            }

            // 책 제목과 리뷰 내용 가져오기
            String title = bookRepository.findTitleById(itemId).orElse("제목 없음");
            String content = reviewRepository.findContentById(itemId).orElse("내용 없음");
            String image = bookRepository.findImageById(itemId).orElse("제목 없음");

            rankingList.add(new RankingDTO(rank, title, content, score, image));

            prevScore = score;
            index++;

            if (rankingType == RankingType.DAILY_REVIEWS && index >= 5) break;
        }

        return rankingList;
    }
}
