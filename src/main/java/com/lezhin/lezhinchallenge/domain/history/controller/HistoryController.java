package com.lezhin.lezhinchallenge.domain.history;


import com.lezhin.domain.history.dto.HistoryResponseDto;
import com.lezhin.domain.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 조회 이력 목록
     */
    @GetMapping
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<Page<HistoryResponseDto>> getUserViewHistory(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "viewedAt") Pageable pageable) {

        return ResponseEntity.ok(historyService.getUserViewHistory(userId, pageable));
    }

    /**
     * 사용자의 특정 작품 조회 이력 삭제
     * @param userId 사용자 ID
     * @param historyId 이력 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{historyId}")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteViewHistory(
            @PathVariable Long userId,
            @PathVariable Long historyId) {

        historyService.deleteViewHistory(userId, historyId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자의 모든 조회 이력 삭제
     * @param userId 사용자 ID
     * @return 204 No Content
     */
    @DeleteMapping
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAllViewHistory(@PathVariable Long userId) {
        historyService.deleteAllViewHistory(userId);
        return ResponseEntity.noContent().build();
    }
}