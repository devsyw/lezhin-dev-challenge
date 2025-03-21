package com.lezhin.lezhinchallenge.domain.work.dto;

import com.lezhin.lezhinchallenge.domain.work.entity.Work;
import com.lezhin.lezhinchallenge.domain.work.entity.WorkType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WorkDto {
    /**
     * 작품 생성 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkCreateRequestDto {

        @NotBlank(message = "제목은 필수입니다")
        private String title;

        @NotBlank(message = "작가는 필수입니다")
        private String author;

        private String description;

        @NotNull(message = "가격은 필수입니다")
        @DecimalMin(value = "0.0", message = "가격은 0 이상이어야 합니다")
        private BigDecimal price;

        @NotNull(message = "타입은 필수입니다")
        private WorkType type;

        private String thumbnailUrl;

        /**
         * DTO를 엔티티로 변환
         */
        public Work toEntity() {
            return Work.builder()
                    .title(title)
                    .author(author)
                    .description(description)
                    .price(price)
                    .type(type)
                    .thumbnailUrl(thumbnailUrl)
                    .build();
        }
    }

    /**
     * 작품 수정 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkUpdateRequestDto {

        @NotBlank(message = "제목은 필수입니다")
        private String title;

        @NotBlank(message = "작가는 필수입니다")
        private String author;

        private String description;

        @NotNull(message = "가격은 필수입니다")
        @DecimalMin(value = "0.0", message = "가격은 0 이상이어야 합니다")
        private BigDecimal price;

        @NotNull(message = "타입은 필수입니다")
        private WorkType type;

        private String thumbnailUrl;
    }

    /**
     * 작품 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkResponseDto {

        private Long id;
        private String title;
        private String author;
        private String description;
        private BigDecimal price;
        private WorkType type;
        private String thumbnailUrl;
        private int viewCount;
        private int purchaseCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;

        /**
         * 엔티티를 DTO로 변환
         */
        public static WorkResponseDto from(Work work) {
            return WorkResponseDto.builder()
                    .id(work.getId())
                    .title(work.getTitle())
                    .author(work.getAuthor())
                    .description(work.getDescription())
                    .price(work.getPrice())
                    .type(work.getType())
                    .thumbnailUrl(work.getThumbnailUrl())
                    .viewCount(work.getViewCount())
                    .purchaseCount(work.getPurchaseCount())
                    .createdAt(work.getCreatedAt())
                    .updatedAt(work.getUpdatedAt())
                    .createdBy(work.getCreatedBy())
                    .build();
        }
    }
}
