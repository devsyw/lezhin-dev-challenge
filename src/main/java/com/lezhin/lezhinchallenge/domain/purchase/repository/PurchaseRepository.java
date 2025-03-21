package com.lezhin.lezhinchallenge.domain.purchase.repository;


import com.lezhin.lezhinchallenge.domain.purchase.entity.Purchase;
import com.lezhin.lezhinchallenge.domain.purchase.entity.PurchaseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 작품 구매 내역 리포지토리
 */
@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    /**
     * 사용자별 구매 내역 목록 조회
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 사용자별 구매 내역 목록
     */
    Page<Purchase> findByUserId(Long userId, Pageable pageable);

    /**
     * 작품별 구매 내역 목록 조회
     * @param workId 작품 ID
     * @param pageable 페이징 정보
     * @return 작품별 구매 내역 목록
     */
    Page<Purchase> findByWorkId(Long workId, Pageable pageable);

    /**
     * 사용자의 특정 구매 내역 조회
     * @param id 구매 ID
     * @param userId 사용자 ID
     * @return 구매 내역
     */
    Optional<Purchase> findByIdAndUserId(Long id, Long userId);

    /**
     * 사용자가 특정 작품을 구매했는지 확인
     * @param userId 사용자 ID
     * @param workId 작품 ID
     * @return 구매 여부
     */
    boolean existsByUserIdAndWorkId(Long userId, Long workId);

    /**
     * 구매 타입별 구매 내역 조회
     * @param type 구매 타입
     * @param pageable 페이징 정보
     * @return 구매 타입별 내역 목록
     */
    Page<Purchase> findByType(PurchaseType type, Pageable pageable);

    /**
     * 특정 기간 내 구매 내역 조회
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 기간 내 구매 내역 목록
     */
    List<Purchase> findByPurchasedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 가격 이상의 구매 내역 조회
     * @param price 가격
     * @param pageable 페이징 정보
     * @return 가격 이상 구매 내역 목록
     */
    Page<Purchase> findByPriceGreaterThanEqual(BigDecimal price, Pageable pageable);

    /**
     * 사용자별 총 구매 금액 조회
     * @param userId 사용자 ID
     * @return 총 구매 금액
     */
    @Query("SELECT SUM(p.price) FROM Purchase p WHERE p.user.id = :userId")
    BigDecimal getTotalPurchaseAmountByUserId(@Param("userId") Long userId);

    /**
     * 특정 기간 내 가장 많이 구매된 작품 목록
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @param limit 조회 개수
     * @return 기간 내 인기 구매 작품 ID 목록
     */
    @Query(value = "SELECT p.work_id, COUNT(p.id) as purchase_count " +
            "FROM purchases p " +
            "WHERE p.purchased_at BETWEEN :startDate AND :endDate " +
            "GROUP BY p.work_id " +
            "ORDER BY purchase_count DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Object[]> findMostPurchasedWorksInPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("limit") int limit
    );
}