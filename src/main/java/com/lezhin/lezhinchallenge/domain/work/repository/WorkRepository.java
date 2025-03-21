package com.lezhin.lezhinchallenge.domain.work.repository;

import com.lezhin.lezhinchallenge.domain.work.entity.Work;
import com.lezhin.lezhinchallenge.domain.work.entity.WorkType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 작품 정보 리포지토리
 */
@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

    /**
     * 조회수 기준 인기 작품 상위 10개 조회
     * @return 인기 작품 목록
     */
    List<Work> findTop10ByOrderByViewCountDesc();

    /**
     * 구매수 기준 인기 작품 상위 10개 조회
     * @return 인기 구매 작품 목록
     */
    List<Work> findTop10ByOrderByPurchaseCountDesc();

    /**
     * 작가별 작품 목록 조회
     * @param author 작가명
     * @param pageable 페이징 정보
     * @return 작가별 작품 목록
     */
    Page<Work> findByAuthor(String author, Pageable pageable);

    /**
     * 작품 타입별 목록 조회
     * @param type 작품 타입
     * @param pageable 페이징 정보
     * @return 타입별 작품 목록
     */
    Page<Work> findByType(WorkType type, Pageable pageable);

    /**
     * 제목 키워드로 작품 검색
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 검색 결과 작품 목록
     */
    @Query("SELECT w FROM Work w WHERE w.title LIKE %:keyword%")
    Page<Work> searchByTitleContaining(String keyword, Pageable pageable);

    /**
     * 무료 작품 목록 조회
     * @param pageable 페이징 정보
     * @return 무료 작품 목록
     */
    Page<Work> findByPriceEquals(java.math.BigDecimal price, Pageable pageable);
}