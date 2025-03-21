package com.lezhin.lezhinchallenge.domain.purchase.controller;

import com.lezhin.lezhinchallenge.domain.purchase.dto.PurchaseDto;
import com.lezhin.lezhinchallenge.domain.purchase.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * 사용자의 구매 내역 조회
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 구매 내역 목록
     */
    @GetMapping
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<Page<PurchaseDto.PurchaseResponseDto>> getUserPurchases(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "purchasedAt") Pageable pageable) {

        return ResponseEntity.ok(purchaseService.getUserPurchases(userId, pageable));
    }

    /**
     * 작품 구매
     * @param userId 사용자 ID
     * @param requestDto 구매 요청 정보
     * @return 구매 정보
     */
    @PostMapping
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<PurchaseDto.PurchaseResponseDto> purchaseWork(
            @PathVariable Long userId,
            @Valid @RequestBody PurchaseDto.PurchaseRequestDto requestDto) {

        PurchaseDto.PurchaseResponseDto purchase = purchaseService.purchaseWork(userId, requestDto);
        return ResponseEntity
                .created(URI.create("/api/users/" + userId + "/purchases/" + purchase.getId()))
                .body(purchase);
    }

    /**
     * 특정 구매 내역 조회
     * @param userId 사용자 ID
     * @param purchaseId 구매 ID
     * @return 구매 정보
     */
    @GetMapping("/{purchaseId}")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<PurchaseDto.PurchaseResponseDto> getUserPurchase(
            @PathVariable Long userId,
            @PathVariable Long purchaseId) {

        return ResponseEntity.ok(purchaseService.getUserPurchase(userId, purchaseId));
    }
}