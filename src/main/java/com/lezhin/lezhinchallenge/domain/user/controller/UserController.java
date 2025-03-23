package com.lezhin.lezhinchallenge.domain.user.controller;

import com.lezhin.lezhinchallenge.common.exception.custom.InsufficientPermissionException;
import com.lezhin.lezhinchallenge.domain.user.dto.UserDto;
import com.lezhin.lezhinchallenge.domain.user.entity.UserRole;
import com.lezhin.lezhinchallenge.domain.user.service.UserService;
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

/**
 * 사용자 관련 API 엔드포인트
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 사용자 목록 조회 (관리자 전용)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto.UserResponseDto>> getUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(userService.getUsers(pageable));
    }

    /**
     * 특정 사용자 조회
     */
    @GetMapping("/{userId}")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<UserDto.UserResponseDto> getUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {

        validateUserAccess(userDetails, userId);

        return ResponseEntity.ok(userService.getUser(userId));
    }

    /**
     * 사용자 정보 수정
     */
    @PutMapping("/{userId}")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<UserDto.UserResponseDto> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserDto.UserRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long currentUserId;
        try {
            currentUserId = Long.parseLong(userDetails.getUsername());
        } catch (NumberFormatException e) {
            throw new InsufficientPermissionException("잘못된 인증 정보입니다");
        }

        return ResponseEntity.ok(userService.updateUser(userId, requestDto, currentUserId));
    }

    /**
     * 사용자 포인트 충전
     */
    @PostMapping("/{userId}/points")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<UserDto.UserResponseDto> chargePoint(
            @PathVariable Long userId,
            @Valid @RequestBody UserDto.PointChargeRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 권한 체크 (인증 정보의 ID와 요청된 userId가 일치하는지)
        validateUserAccess(userDetails, userId);

        return ResponseEntity.ok(userService.chargePoint(userId, requestDto));
    }

    /**
     * 사용자 권한 추가 (관리자 전용)
     */
    @PostMapping("/{userId}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto.UserResponseDto> addRole(
            @PathVariable Long userId,
            @PathVariable UserRole role) {
        return ResponseEntity.ok(userService.addRole(userId, role));
    }

    /**
     * 사용자 권한 삭제 (관리자 전용)
     */
    @DeleteMapping("/{userId}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto.UserResponseDto> removeRole(
            @PathVariable Long userId,
            @PathVariable UserRole role) {
        return ResponseEntity.ok(userService.removeRole(userId, role));
    }

    /**
     * 사용자 활성화/비활성화 (관리자 전용)
     */
    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto.UserResponseDto> setUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean enable) {
        return ResponseEntity.ok(userService.setUserEnabled(userId, enable));
    }

    /**
     * 사용자명으로 사용자 검색 (관리자 전용)
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto.UserResponseDto>> searchUsers(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.searchUsersByNickname(keyword, pageable));
    }

    /**
     * 특정 권한을 가진 사용자 목록 조회 (관리자 전용)
     */
    @GetMapping("/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto.UserResponseDto>> getUsersByRole(
            @PathVariable UserRole role,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersByRole(role, pageable));
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto.UserResponseDto> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId;
        try {
            userId = Long.parseLong(userDetails.getUsername());
        } catch (NumberFormatException e) {
            throw new InsufficientPermissionException("잘못된 인증 정보입니다");
        }

        return ResponseEntity.ok(userService.getUser(userId));
    }

    /**
     * 사용자 권한 확인 - 본인이거나 관리자인지 검증
     */
    private void validateUserAccess(UserDetails userDetails, Long userId) {
        Long currentUserId;
        try {
            currentUserId = Long.parseLong(userDetails.getUsername());
        } catch (NumberFormatException e) {
            throw new InsufficientPermissionException("잘못된 인증 정보입니다");
        }

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!currentUserId.equals(userId) && !isAdmin) {
            throw new InsufficientPermissionException("다른 사용자의 정보에 접근할 권한이 없습니다");
        }
    }
}