package com.lezhin.lezhinchallenge.domain.user.repository;

import com.lezhin.lezhinchallenge.domain.user.entity.User;
import com.lezhin.lezhinchallenge.domain.user.entity.UserRole;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 사용자 레포지토리
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 사용자 조회
     * @param username
     * @return 조회된 사용자
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자 조회
     * @param email
     * @return 조회된 사용자
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자명 존재 여부 확인
     * @param username
     * @return 존재 여부
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 존재 여부 확인
     * @param email
     * @return 존재 여부
     */
    boolean existsByEmail(String email);


    /**
     * 특정 권한을 가진 사용자 목록 조회
     */
    @Query("SELECT u FROM User u WHERE :role MEMBER OF u.roles")
    Page<User> findByRole(@Param("role") UserRole role, Pageable pageable);

    /**
     * 포인트 순으로 사용자 목록 조회
     */
    Page<User> findAllByOrderByPointDesc(Pageable pageable);

    /**
     * 닉네임으로 사용자 검색
     */
    Page<User> findByNicknameContaining(String keyword, Pageable pageable);

    /**
     * 특정 일자 이후에 가입한 사용자 목록 조회
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date")
    Page<User> findByCreatedAtAfter(@Param("date") LocalDateTime date, Pageable pageable);

    /**
     * 최근 구매한 작품이 있는 활성 사용자 목록
     * (데이터베이스 방언에 맞게 날짜 연산 수정 필요)
     */
    @Query(value = "SELECT DISTINCT u.* FROM users u " +
            "JOIN purchases p ON u.id = p.user_id " +
            "WHERE p.purchased_at >= CURRENT_TIMESTAMP - INTERVAL ':days' DAY " +
            "AND u.enabled = true",
            nativeQuery = true)
    Page<User> findActiveUsersByRecentPurchase(@Param("days") int days, Pageable pageable);
}