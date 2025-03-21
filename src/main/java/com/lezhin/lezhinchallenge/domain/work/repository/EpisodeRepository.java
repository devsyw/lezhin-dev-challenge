package com.lezhin.lezhinchallenge.domain.work.repository;

import com.lezhin.lezhinchallenge.domain.work.entity.Episode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 에피소드 정보 리포지토리
 */
@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    /**
     * 작품별 에피소드 목록 조회
     * @param workId 작품 ID
     * @param pageable 페이징 정보
     * @return 작품별 에피소드 목록
     */
    Page<Episode> findByWorkId(Long workId, Pageable pageable);

    /**
     * 작품별 에피소드 목록 조회 (에피소드 번호 순)
     * @param workId 작품 ID
     * @return 작품별 에피소드 목록
     */
    List<Episode> findByWorkIdOrderByEpisodeNumberAsc(Long workId);

    /**
     * 작품의 특정 에피소드 조회
     * @param workId 작품 ID
     * @param episodeNumber 에피소드 번호
     * @return 특정 에피소드
     */
    Optional<Episode> findByWorkIdAndEpisodeNumber(Long workId, int episodeNumber);

    /**
     * 무료 에피소드 목록 조회
     * @param workId 작품 ID
     * @return 무료 에피소드 목록
     */
    List<Episode> findByWorkIdAndIsFreeTrue(Long workId);

    /**
     * 작품의 최신 에피소드 조회
     * @param workId 작품 ID
     * @return 최신 에피소드
     */
    Optional<Episode> findTopByWorkIdOrderByEpisodeNumberDesc(Long workId);

    /**
     * 조회수 기준 인기 에피소드 목록 조회
     * @param workId 작품 ID
     * @param limit 조회 개수
     * @return 인기 에피소드 목록
     */
    @Query(value = "SELECT * FROM episodes WHERE work_id = :workId ORDER BY view_count DESC LIMIT :limit",
            nativeQuery = true)
    List<Episode> findTopEpisodesByViewCount(@Param("workId") Long workId, @Param("limit") int limit);
}