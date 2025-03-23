package com.lezhin.lezhinchallenge.domain.purchase.service;

import com.lezhin.lezhinchallenge.common.exception.BaseException;
import com.lezhin.lezhinchallenge.common.exception.ErrorCode;
import com.lezhin.lezhinchallenge.common.exception.custom.*;
import com.lezhin.lezhinchallenge.domain.purchase.dto.PurchaseDto;
import com.lezhin.lezhinchallenge.domain.purchase.entity.Purchase;
import com.lezhin.lezhinchallenge.domain.purchase.entity.PurchaseType;
import com.lezhin.lezhinchallenge.domain.purchase.repository.PurchaseRepository;
import com.lezhin.lezhinchallenge.domain.user.entity.User;
import com.lezhin.lezhinchallenge.domain.user.repository.UserRepository;
import com.lezhin.lezhinchallenge.domain.work.entity.Work;
import com.lezhin.lezhinchallenge.domain.work.repository.WorkRepository;
import com.lezhin.lezhinchallenge.domain.work.service.WorkService;
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
     */
    public Page<PurchaseDto.PurchaseResponseDto> getUserPurchases(Long userId, Pageable pageable) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다");
        }

        return purchaseRepository.findByUserId(userId, pageable)
                .map(PurchaseDto.PurchaseResponseDto::from);
    }

    /**
     * 특정 구매 내역 조회
     */
    public PurchaseDto.PurchaseResponseDto getUserPurchase(Long userId, Long purchaseId) {
        Purchase purchase = purchaseRepository.findByIdAndUserId(purchaseId, userId)
                .orElseThrow(() -> new PurchaseNotFoundException(
                        "ID가 " + purchaseId + "인 구매 내역을 찾을 수 없거나 사용자의 구매 내역이 아닙니다"));

        return PurchaseDto.PurchaseResponseDto.from(purchase);
    }

    /**
     * 작품 구매
     */
    @Transactional
    public PurchaseDto.PurchaseResponseDto purchaseWork(Long userId, PurchaseDto.PurchaseRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다"));

        Work work = workRepository.findById(requestDto.getWorkId())
                .orElseThrow(() -> new WorkNotFoundException("ID가 " + requestDto.getWorkId() + "인 작품을 찾을 수 없습니다"));

        // 이미 구매한 작품인지 확인
        if (purchaseRepository.existsByUserIdAndWorkId(userId, requestDto.getWorkId())) {
            throw new AlreadyPurchasedException("이미 구매한 작품입니다");
        }

        // 구매 타입에 따른 처리
        if (requestDto.getType() == PurchaseType.FREE) {
            // 무료 작품이 아닌 경우 예외 발생
            if (work.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                throw new BaseException(ErrorCode.INVALID_INPUT_VALUE, "유료 작품은 무료로 구매할 수 없습니다");
            }
        } else if (requestDto.getType() == PurchaseType.POINT) {
            // 포인트 차감
            if (!user.usePoint(work.getPrice().intValue())) {
                throw new NotEnoughPointException("작품 구매를 위한 포인트가 부족합니다");
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