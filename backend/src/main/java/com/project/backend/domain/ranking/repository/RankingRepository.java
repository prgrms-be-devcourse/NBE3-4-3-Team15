package com.project.backend.domain.ranking.repository;

import com.project.backend.domain.ranking.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * -- 랭킹 레포지토리 --
 *
 * @author -- 김남우 --
 * @since -- 3월 4일 --
 */
public interface RankingRepository extends JpaRepository<Ranking, Long> {

    List<Ranking> findTop10ByTypeOrderByScoreDesc(String type);

}
