package com.lezhin.lezhinchallenge.domain.purchase.controller;

import com.lezhin.lezhinchallenge.common.exception.custom.InsufficientPermissionException;
import com.lezhin.lezhinchallenge.domain.purchase.dto.PurchaseDto;
import com.lezhin.lezhinchallenge.domain.purchase.service.PurchaseService;
import com.lezhin.lezhinchallenge.domain.user.entity.User;
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

/**
 * 작품 구매 관련 API 엔드포인트
 */
@RestController
@RequestMapping("/api/users/{userId}/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    /**
     * 사용자의 구매내역 조회
     */
    @GetMapping
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<Page<PurchaseDto.PurchaseResponseDto>> getUserPurchases(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "purchasedAt") Pageable pageable) {

        validateUserAccess(userDetails, userId);

        return ResponseEntity.ok(purchaseService.getUserPurchases(userId, pageable));
    }

    /**
     * 작품 구매
     */
    @PostMapping
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<PurchaseDto.PurchaseResponseDto> purchaseWork(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PurchaseDto.PurchaseRequestDto requestDto) {

        // 권한 체크 (인증 정보의 ID와 요청된 userId가 일치하는지)
        validateUserAccess(userDetails, userId);

        PurchaseDto.PurchaseResponseDto purchase = purchaseService.purchaseWork(userId, requestDto);
        return ResponseEntity
                .created(URI.create("/api/users/" + userId + "/purchases/" + purchase.getId()))
                .body(purchase);
    }

    /**
     * 특정 구매내역 조회
     */
    @GetMapping("/{purchaseId}")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<PurchaseDto.PurchaseResponseDto> getUserPurchase(
            @PathVariable Long userId,
            @PathVariable Long purchaseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 권한 체크 (인증 정보의 ID와 요청된 userId가 일치하는지)
        validateUserAccess(userDetails, userId);

        return ResponseEntity.ok(purchaseService.getUserPurchase(userId, purchaseId));
    }

    /**
     * 사용자 권한확인 - 본인이거나 관리자인지 검증
     */
    private void validateUserAccess(UserDetails userDetails, Long userId) {
        User currentUser = (User) userDetails;
        Long currentUserId = currentUser.getId();  // 직접 ID 접근

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!currentUserId.equals(userId) && !isAdmin) {
            throw new InsufficientPermissionException("다른 사용자의 정보에 접근할 권한이 없습니다");
        }
    }
}