package com.lezhin.lezhinchallenge.domain.user.dto;

import com.lezhin.lezhinchallenge.domain.user.entity.User;
import com.lezhin.lezhinchallenge.domain.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

public class UserDto {

    /**
     * 사용자 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRequestDto {

        @NotBlank(message = "사용자명은 필수입니다")
        @Size(min = 3, max = 20, message = "사용자명은 3자 이상 50자 이하여야 합니다")
        private String username;

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다")
        private String password;

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "유효한 이메일 형식이어야 합니다")
        private String email;

        @Size(max = 20, message = "닉네임은 20자 이하여야 합니다")
        private String nickname;
    }

    /**
     * 사용자 정보 수정 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserUpdateRequestDto {

        @Size(min = 3, max = 20, message = "사용자명은 3자 이상 20자 이하여야 합니다")
        private String username;

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다")
        private String password;

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "유효한 이메일 형식이어야 합니다")
        private String email;

        @Size(max = 20, message = "닉네임은 20자 이하여야 합니다")
        private String nickname;
    }

    /**
     * 사용자 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponseDto {

        private Long id;
        private String username;
        private String email;
        private String nickname;
        private int point;
        private Set<UserRole> roles;
        private boolean enabled;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        /**
         * 엔티티를 DTO로 변환
         */
        public static UserResponseDto from(User user) {
            return UserResponseDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .point(user.getPoint())
                    .roles(user.getRoles())
                    .enabled(user.isEnabled())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        }
    }

    /**
     * 사용자 포인트 충전 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointChargeRequestDto {

        @NotBlank(message = "충전 금액은 필수입니다")
        private int amount;
    }
}
