package com.lezhin.lezhinchallenge.domain.work.dto;

import com.lezhin.lezhinchallenge.domain.work.entity.Episode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
public class EpisodeDto {

    /**
     * 에피소드 생성 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EpisodeCreateRequestDto {

        @NotNull(message = "작품 ID는 필수입니다")
        private Long workId;

        @NotBlank(message = "제목은 필수입니다")
        private String title;

        @NotNull(message = "에피소드 번호는 필수입니다")
        @Min(value = 1, message = "에피소드 번호는 1 이상이어야 합니다")
        private int episodeNumber;

        private String content;

        @NotNull(message = "가격은 필수입니다")
        @DecimalMin(value = "0.0", message = "가격은 0 이상이어야 합니다")
        private BigDecimal price;

        @NotNull(message = "무료 여부는 필수입니다")
        private Boolean isFree;
    }

    /**
     * 에피소드 수정 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EpisodeUpdateRequestDto {

        @NotBlank(message = "제목은 필수입니다")
        private String title;

        private String content;

        @NotNull(message = "가격은 필수입니다")
        @DecimalMin(value = "0.0", message = "가격은 0 이상이어야 합니다")
        private BigDecimal price;

        @NotNull(message = "무료 여부는 필수입니다")
        private boolean isFree;
    }

    /**
     * 에피소드 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EpisodeResponseDto {

        private Long id;
        private Long workId;
        private String workTitle;
        private String title;
        private int episodeNumber;
        private String content;
        private BigDecimal price;
        private boolean isFree;
        private int viewCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        /**
         * 엔티티를 DTO로 변환
         */
        public static EpisodeResponseDto from(Episode episode) {
            return EpisodeResponseDto.builder()
                    .id(episode.getId())
                    .workId(episode.getWork().getId())
                    .workTitle(episode.getWork().getTitle())
                    .title(episode.getTitle())
                    .episodeNumber(episode.getEpisodeNumber())
                    .content(episode.getContent())
                    .price(episode.getPrice())
                    .isFree(episode.isFree())
                    .viewCount(episode.getViewCount())
                    .createdAt(episode.getCreatedAt())
                    .updatedAt(episode.getUpdatedAt())
                    .build();
        }
    }
}
