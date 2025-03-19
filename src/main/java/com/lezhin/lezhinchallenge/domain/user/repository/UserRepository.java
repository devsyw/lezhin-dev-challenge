package com.lezhin.lezhinchallenge.domain.user.repository;

import com.lezhin.lezhinchallenge.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}