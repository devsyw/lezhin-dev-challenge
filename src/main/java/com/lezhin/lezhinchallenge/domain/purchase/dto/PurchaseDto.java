package com.lezhin.lezhinchallenge.domain.purchase.dto;

import com.lezhin.lezhinchallenge.domain.purchase.entity.Purchase;
import com.lezhin.lezhinchallenge.domain.purchase.entity.PurchaseType;
import com.lezhin.lezhinchallenge.domain.work.dto.WorkDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
public class PurchaseDto {
    /**
     * 구매 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseRequestDto {

        @NotNull(message = "작품 ID는 필수입니다")
        private Long workId;

        @NotNull(message = "구매 타입은 필수입니다")
        private PurchaseType type;
    }

    /**
     * 구매 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseResponseDto {

        private Long id;
        private Long userId;
        private String username;
        private WorkDto.WorkResponseDto work;
        private BigDecimal price;
        private PurchaseType type;
        private LocalDateTime purchasedAt;
        private LocalDateTime createdAt;

        /**
         * 엔티티를 DTO로 변환
         */
        public static PurchaseResponseDto from(Purchase purchase) {
            return PurchaseResponseDto.builder()
                    .id(purchase.getId())
                    .userId(purchase.getUser().getId())
                    .username(purchase.getUser().getUsername())
                    .work(WorkDto.WorkResponseDto.from(purchase.getWork()))
                    .price(purchase.getPrice())
                    .type(purchase.getType())
                    .purchasedAt(purchase.getPurchasedAt())
                    .createdAt(purchase.getCreatedAt())
                    .build();
        }
    }
}
