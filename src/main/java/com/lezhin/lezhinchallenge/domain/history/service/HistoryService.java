package com.lezhin.lezhinchallenge.domain.history.service;

import com.lezhin.lezhinchallenge.domain.history.dto.HistoryDto;
import com.lezhin.lezhinchallenge.domain.history.entity.History;
import com.lezhin.lezhinchallenge.domain.history.repository.HistoryRepository;
import com.lezhin.lezhinchallenge.domain.user.entity.User;
import com.lezhin.lezhinchallenge.domain.user.repository.UserRepository;
import com.lezhin.lezhinchallenge.domain.work.entity.Work;
import com.lezhin.lezhinchallenge.domain.work.repository.WorkRepository;
import com.lezhin.lezhinchallenge.domain.work.service.WorkService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 작품 조회 이력 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final WorkRepository workRepository;
    private final WorkService workService;

    /**
     * 사용자의 작품 조회 이력 목록 조회
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 조회 이력 목록
     */
    public Page<HistoryDto.HistoryResponseDto> getUserViewHistory(Long userId, Pageable pageable) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        return historyRepository.findByUserId(userId, pageable)
                .map(HistoryDto.HistoryResponseDto::from);
    }

    /**
     * 작품 조회 이력 저장
     * @param workId 작품 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void saveViewHistory(Long workId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new EntityNotFoundException("Work not found with id: " + workId));

        // 이력 저장
        History history = History.builder()
                .user(user)
                .work(work)
                .build();

        historyRepository.save(history);

        // 조회수 증가
        workService.increaseViewCount(workId);
    }

    /**
     * 특정 조회 이력 삭제
     * @param userId 사용자 ID
     * @param historyId 이력 ID
     */
    @Transactional
    public void deleteViewHistory(Long userId, Long historyId) {
        History history = historyRepository.findByIdAndUserId(historyId, userId)
                .orElseThrow(() -> new EntityNotFoundException("History not found with id: " + historyId));

        historyRepository.delete(history);
    }

    /**
     * 사용자의 모든 조회 이력 삭제
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteAllViewHistory(Long userId) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        historyRepository.deleteAllByUserId(userId);
    }
}