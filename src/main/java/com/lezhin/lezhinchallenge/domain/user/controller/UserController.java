package com.lezhin.lezhinchallenge.domain.user.controller;

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
     * @param pageable 페이징 정보
     * @return 사용자 목록
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto.UserResponseDto>> getUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(userService.getUsers(pageable));
    }

    /**
     * 특정 사용자 조회
     * @param userId 사용자 ID
     * @param userDetails 인증된 사용자 정보
     * @return 사용자 정보
     */
    @GetMapping("/{userId}")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<UserDto.UserResponseDto> getUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    /**
     * 사용자 정보 수정
     * @param userId 사용자 ID
     * @param requestDto 사용자 수정 요청 정보
     * @param userDetails 인증된 사용자 정보
     * @return 수정된 사용자 정보
     */
    @PutMapping("/{userId}")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<UserDto.UserResponseDto> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserDto.UserRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(userService.updateUser(userId, requestDto, currentUserId));
    }

    /**
     * 사용자 포인트 충전
     * @param userId 사용자 ID
     * @param requestDto 포인트 충전 요청 정보
     * @return 충전 후 사용자 정보
     */
    @PostMapping("/{userId}/points")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public ResponseEntity<UserDto.UserResponseDto> chargePoint(
            @PathVariable Long userId,
            @Valid @RequestBody UserDto.PointChargeRequestDto requestDto) {
        return ResponseEntity.ok(userService.chargePoint(userId, requestDto));
    }

    /**
     * 사용자 권한 추가 (관리자 전용)
     * @param userId 사용자 ID
     * @param role 추가할 권한
     * @return 권한 추가 후 사용자 정보
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
     * @param userId 사용자 ID
     * @param role 삭제할 권한
     * @return 권한 삭제 후 사용자 정보
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
     * @param userId 사용자 ID
     * @param enable 활성화 여부
     * @return 상태 변경 후 사용자 정보
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
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 검색 결과 사용자 목록
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
     * @param role 권한
     * @param pageable 페이징 정보
     * @return 권한별 사용자 목록
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
     * @param userDetails 인증된 사용자 정보
     * @return 현재 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto.UserResponseDto> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(userService.getUser(userId));
    }
}