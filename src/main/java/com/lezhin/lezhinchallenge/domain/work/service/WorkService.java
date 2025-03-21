package com.lezhin.lezhinchallenge.domain.work.service;

import com.lezhin.lezhinchallenge.domain.history.repository.HistoryRepository;
import com.lezhin.lezhinchallenge.domain.work.dto.WorkDto;
import com.lezhin.lezhinchallenge.domain.work.entity.Work;
import com.lezhin.lezhinchallenge.domain.work.repository.WorkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 작품 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkService {

    private final WorkRepository workRepository;
    private final HistoryRepository historyRepository;

    /**
     * 작품 목록 조회
     * @param pageable 페이징 정보
     * @return 작품 목록 (페이징)
     */
    public Page<WorkDto.WorkResponseDto> getWorks(Pageable pageable) {
        return workRepository.findAll(pageable)
                .map(WorkDto.WorkResponseDto::from);
    }

    /**
     * 특정 작품 조회
     * @param workId 작품 ID
     * @return 작품 정보
     */
    @Cacheable(value = "works", key = "#workId")
    public WorkDto.WorkResponseDto getWork(Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new EntityNotFoundException("Work not found with id: " + workId));

        return WorkDto.WorkResponseDto.from(work);
    }

    /**
     * 인기 작품 목록 조회 (조회수 기준)
     * @return 인기 작품 목록
     */
    @Cacheable(value = "popularWorks")
    public List<WorkDto.WorkResponseDto> getPopularWorks() {
        return workRepository.findTop10ByOrderByViewCountDesc().stream()
                .map(WorkDto.WorkResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 인기 구매 작품 목록 조회 (구매수 기준)
     * @return 인기 구매 작품 목록
     */
    @Cacheable(value = "popularPurchaseWorks")
    public List<WorkDto.WorkResponseDto> getPopularPurchaseWorks() {
        return workRepository.findTop10ByOrderByPurchaseCountDesc().stream()
                .map(WorkDto.WorkResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 작품 등록
     * @param requestDto 작품 등록 요청 정보
     * @return 등록된 작품 정보
     */
    @Transactional
    public WorkDto.WorkResponseDto createWork(WorkDto.WorkCreateRequestDto requestDto) {
        Work work = requestDto.toEntity();
        Work savedWork = workRepository.save(work);

        return WorkDto.WorkResponseDto.from(savedWork);
    }

    /**
     * 작품 수정
     * @param workId 작품 ID
     * @param requestDto 작품 수정 요청 정보
     * @return 수정된 작품 정보
     */
    @Transactional
    @CacheEvict(value = {"works", "popularWorks", "popularPurchaseWorks"}, key = "#workId")
    public WorkDto.WorkResponseDto updateWork(Long workId, WorkDto.WorkUpdateRequestDto requestDto) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new EntityNotFoundException("Work not found with id: " + workId));

        work.update(
                requestDto.getTitle(),
                requestDto.getAuthor(),
                requestDto.getDescription(),
                requestDto.getPrice(),
                requestDto.getType(),
                requestDto.getThumbnailUrl()
        );

        return WorkDto.WorkResponseDto.from(work);
    }

    /**
     * 작품 삭제 (관련 이력도 함께 삭제)
     * @param workId 작품 ID
     */
    @Transactional
    @CacheEvict(value = {"works", "popularWorks", "popularPurchaseWorks"}, allEntries = true)
    public void deleteWork(Long workId) {
        // 작품 존재 여부 확인
        if (!workRepository.existsById(workId)) {
            throw new EntityNotFoundException("Work not found with id: " + workId);
        }

        // 관련 이력 삭제
        historyRepository.deleteAllByWorkId(workId);

        // 작품 삭제
        workRepository.deleteById(workId);
    }

    /**
     * 작품 조회수 증가
     * @param workId 작품 ID
     */
    @Transactional
    @CacheEvict(value = {"works", "popularWorks"}, key = "#workId")
    public void increaseViewCount(Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new EntityNotFoundException("Work not found with id: " + workId));

        work.increaseViewCount();
    }

    /**
     * 작품 구매수 증가
     * @param workId 작품 ID
     */
    @Transactional
    @CacheEvict(value = {"works", "popularPurchaseWorks"}, key = "#workId")
    public void increasePurchaseCount(Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new EntityNotFoundException("Work not found with id: " + workId));

        work.increasePurchaseCount();
    }
}