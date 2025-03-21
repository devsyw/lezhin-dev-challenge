package com.lezhin.lezhinchallenge.domain.purchase.service;

import com.lezhin.lezhinchallenge.domain.purchase.dto.PurchaseDto;
import com.lezhin.lezhinchallenge.domain.purchase.entity.Purchase;
import com.lezhin.lezhinchallenge.domain.purchase.entity.PurchaseType;
import com.lezhin.lezhinchallenge.domain.purchase.repository.PurchaseRepository;
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

import java.math.BigDecimal;

/**
 * 작품 구매 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final WorkRepository workRepository;
    private final WorkService workService;

    /**
     * 사용자의 구매 내역 조회
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 구매 내역 목록
     */
    public Page<PurchaseDto.PurchaseResponseDto> getUserPurchases(Long userId, Pageable pageable) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        return purchaseRepository.findByUserId(userId, pageable)
                .map(PurchaseDto.PurchaseResponseDto::from);
    }

    /**
     * 특정 구매 내역 조회
     * @param userId 사용자 ID
     * @param purchaseId 구매 ID
     * @return 구매 정보
     */
    public PurchaseDto.PurchaseResponseDto getUserPurchase(Long userId, Long purchaseId) {
        Purchase purchase = purchaseRepository.findByIdAndUserId(purchaseId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found with id: " + purchaseId));

        return PurchaseDto.PurchaseResponseDto.from(purchase);
    }

    /**
     * 작품 구매
     * @param userId 사용자 ID
     * @param requestDto 구매 요청 정보
     * @return 구매 정보
     */
    @Transactional
    public PurchaseDto.PurchaseResponseDto purchaseWork(Long userId, PurchaseDto.PurchaseRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Work work = workRepository.findById(requestDto.getWorkId())
                .orElseThrow(() -> new EntityNotFoundException("Work not found with id: " + requestDto.getWorkId()));

        // 이미 구매한 작품인지 확인
        if (purchaseRepository.existsByUserIdAndWorkId(userId, requestDto.getWorkId())) {
            throw new IllegalStateException("User has already purchased this work");
        }

        // 구매 타입에 따른 처리
        if (requestDto.getType() == PurchaseType.FREE) {
            // 무료 작품이 아닌 경우 예외 발생
            if (work.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                throw new IllegalStateException("Cannot purchase paid work with FREE type");
            }
        } else if (requestDto.getType() == PurchaseType.POINT) {
            // 포인트 차감
            if (!user.usePoint(work.getPrice().intValue())) {
                throw new IllegalStateException("Not enough points to purchase this work");
            }
        }

        // 구매 정보 저장
        Purchase purchase = Purchase.builder()
                .user(user)
                .work(work)
                .price(work.getPrice())
                .type(requestDto.getType())
                .build();

        Purchase savedPurchase = purchaseRepository.save(purchase);

        // 작품 구매수 증가
        workService.increasePurchaseCount(work.getId());

        return PurchaseDto.PurchaseResponseDto.from(savedPurchase);
    }
}