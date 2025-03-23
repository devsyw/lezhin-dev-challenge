package com.lezhin.lezhinchallenge.domain.history.controller;


import com.lezhin.lezhinchallenge.common.exception.custom.InsufficientPermissionException;
import com.lezhin.lezhinchallenge.domain.history.dto.HistoryDto;
import com.lezhin.lezhinchallenge.domain.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 작품 조회 이력 관련 API 엔드포인트
 */
@RestController
@RequestMapping("/api/users/{userId}/view-history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    /**
     * 사용자의 작품 조회 이력 목록 조회
     */
    @GetMapping
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<Page<HistoryDto.HistoryResponseDto>> getUserViewHistory(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "viewedAt") Pageable pageable) {

        validateUserAccess(userDetails, userId);

        return ResponseEntity.ok(historyService.getUserViewHistory(userId, pageable));
    }

    /**
     * 사용자의 특정 작품 조회 이력 삭제
     */
    @DeleteMapping("/{historyId}")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteViewHistory(
            @PathVariable Long userId,
            @PathVariable Long historyId,
            @AuthenticationPrincipal UserDetails userDetails) {

        validateUserAccess(userDetails, userId);

        historyService.deleteViewHistory(userId, historyId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자의 모든 조회 이력 삭제
     */
    @DeleteMapping
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAllViewHistory(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {

        validateUserAccess(userDetails, userId);

        historyService.deleteAllViewHistory(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자 권한 확인 - 본인이거나 관리자인지 검증
     */
    private void validateUserAccess(UserDetails userDetails, Long userId) {
        Long currentUserId;
        try {
            currentUserId = Long.parseLong(userDetails.getUsername());
        } catch (NumberFormatException e) {
            throw new InsufficientPermissionException("잘못된 인증 정보입니다");
        }
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!currentUserId.equals(userId) && !isAdmin) {
            throw new InsufficientPermissionException("다른 사용자의 조회 이력에 접근할 권한이 없습니다");
        }
    }
}