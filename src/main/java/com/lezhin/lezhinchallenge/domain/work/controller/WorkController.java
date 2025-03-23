package com.lezhin.lezhinchallenge.domain.work.controller;


import com.lezhin.lezhinchallenge.domain.history.service.HistoryService;
import com.lezhin.lezhinchallenge.domain.work.dto.WorkDto;
import com.lezhin.lezhinchallenge.domain.work.service.WorkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * 작품 관련 API 엔드포인트
 */
@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;
    private final HistoryService historyService;

    /**
     * 작품 목록 조회
     */
    @GetMapping
    public ResponseEntity<Page<WorkDto.WorkResponseDto>> getWorks(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(workService.getWorks(pageable));
    }

    /**
     * 특정 작품 조회
     */
    @GetMapping("/{workId}")
    public ResponseEntity<WorkDto.WorkResponseDto> getWork(
            @PathVariable Long workId,
            @AuthenticationPrincipal UserDetails userDetails) {

        WorkDto.WorkResponseDto work = workService.getWork(workId);

        // 인증된 사용자의 경우 조회 이력 저장
        if (userDetails != null) {
            try {
                Long userId = Long.parseLong(userDetails.getUsername());
                historyService.saveViewHistory(workId, userId);
            } catch (NumberFormatException e) {
                // 유저명이 숫자가 아닌 경우 처리 (로깅 등)
                // 실제로는 에러를 더 적절하게 처리해야 함
            }
        }

        return ResponseEntity.ok(work);
    }

    /**
     * 인기 작품 목록 조회 (조회수 기준)
     */
    @GetMapping("/popular")
    public ResponseEntity<List<WorkDto.WorkResponseDto>> getPopularWorks() {
        return ResponseEntity.ok(workService.getPopularWorks());
    }

    /**
     * 인기 구매 작품 목록 조회 (구매수 기준)
     */
    @GetMapping("/popular-purchases")
    public ResponseEntity<List<WorkDto.WorkResponseDto>> getPopularPurchaseWorks() {
        return ResponseEntity.ok(workService.getPopularPurchaseWorks());
    }

    /**
     * 작품 등록
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    public ResponseEntity<WorkDto.WorkResponseDto> createWork(
            @Valid @RequestBody WorkDto.WorkCreateRequestDto requestDto) {

        WorkDto.WorkResponseDto createdWork = workService.createWork(requestDto);
        return ResponseEntity
                .created(URI.create("/api/works/" + createdWork.getId()))
                .body(createdWork);
    }

    /**
     * 작품 수정
     */
    @PutMapping("/{workId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    public ResponseEntity<WorkDto.WorkResponseDto> updateWork(
            @PathVariable Long workId,
            @Valid @RequestBody WorkDto.WorkUpdateRequestDto requestDto) {

        return ResponseEntity.ok(workService.updateWork(workId, requestDto));
    }

    /**
     * 작품 삭제 (관련 이력도 함께 삭제)
     */
    @DeleteMapping("/{workId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteWork(@PathVariable Long workId) {
        workService.deleteWork(workId);
        return ResponseEntity.noContent().build();
    }
}