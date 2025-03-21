package com.lezhin.lezhinchallenge.domain.purchase.dto;

import com.lezhin.lezhinchallenge.domain.history.entity.History;
import com.lezhin.lezhinchallenge.domain.work.dto.WorkDto.WorkResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class HistoryDto {

    /**
     * 조회 이력 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryResponseDto {

        private Long id;
        private Long userId;
        private String username;
        private WorkResponseDto work;
        private LocalDateTime viewedAt;
        private LocalDateTime createdAt;

        /**
         * 엔티티를 DTO로 변환
         */
        public static HistoryResponseDto from(History history) {
            return HistoryResponseDto.builder()
                    .id(history.getId())
                    .userId(history.getUser().getId())
                    .username(history.getUser().getUsername())
                    .work(WorkResponseDto.from(history.getWork()))
                    .viewedAt(history.getViewedAt())
                    .createdAt(history.getCreatedAt())
                    .build();
        }
    }
}
