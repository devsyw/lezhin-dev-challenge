package com.lezhin.lezhinchallenge.domain.history.repository;

import com.lezhin.lezhinchallenge.domain.history.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 작품 조회 이력 리포지토리
 */
@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    /**
     * 사용자별 조회 이력 목록 조회
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 사용자별 조회 이력 목록
     */
    Page<History> findByUserId(Long userId, Pageable pageable);

    /**
     * 작품별 조회 이력 목록 조회
     * @param workId 작품 ID
     * @param pageable 페이징 정보
     * @return 작품별 조회 이력 목록
     */
    Page<History> findByWorkId(Long workId, Pageable pageable);

    /**
     * 사용자의 특정 이력 조회
     * @param id 이력 ID
     * @param userId 사용자 ID
     * @return 조회 이력
     */
    Optional<History> findByIdAndUserId(Long id, Long userId);

    /**
     * 사용자가 특정 작품을 조회한 이력이 있는지 확인
     * @param userId 사용자 ID
     * @param workId 작품 ID
     * @return 조회 여부
     */
    boolean existsByUserIdAndWorkId(Long userId, Long workId);

    /**
     * 특정 작품의 모든 조회 이력 삭제
     * @param workId 작품 ID
     */
    @Modifying
    void deleteAllByWorkId(Long workId);

    /**
     * 특정 사용자의 모든 조회 이력 삭제
     * @param userId 사용자 ID
     */
    @Modifying
    void deleteAllByUserId(Long userId);

    /**
     * 특정 기간 내 사용자의 조회 이력 조회
     * @param userId 사용자 ID
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 기간 내 조회 이력 목록
     */
    @Query("SELECT h FROM History h WHERE h.user.id = :userId AND h.viewedAt BETWEEN :startDate AND :endDate")
    List<History> findByUserIdAndViewedAtBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 특정 기간 내 가장 많이 조회된 작품 목록
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @param limit 조회 개수
     * @return 기간 내 인기 작품 ID 목록
     */
    @Query(value = "SELECT h.work_id, COUNT(h.id) as view_count " +
            "FROM histories h " +
            "WHERE h.viewed_at BETWEEN :startDate AND :endDate " +
            "GROUP BY h.work_id " +
            "ORDER BY view_count DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Object[]> findMostViewedWorksInPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("limit") int limit
    );
}