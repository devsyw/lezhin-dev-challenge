package com.lezhin.lezhinchallenge.domain.user.service;

import com.lezhin.lezhinchallenge.common.exception.BaseException;
import com.lezhin.lezhinchallenge.common.exception.ErrorCode;
import com.lezhin.lezhinchallenge.common.exception.custom.DuplicateValueException;
import com.lezhin.lezhinchallenge.common.exception.custom.InsufficientPermissionException;
import com.lezhin.lezhinchallenge.common.exception.custom.UserNotFoundException;
import com.lezhin.lezhinchallenge.domain.user.dto.UserDto;
import com.lezhin.lezhinchallenge.domain.user.entity.User;
import com.lezhin.lezhinchallenge.domain.user.entity.UserRole;
import com.lezhin.lezhinchallenge.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 목록 조회
     */
    public Page<UserDto.UserResponseDto> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserDto.UserResponseDto::from);
    }

    /**
     * 특정 사용자 조회
     */
    public UserDto.UserResponseDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다"));

        return UserDto.UserResponseDto.from(user);
    }

    /**
     * 사용자 정보 수정
     */
    @Transactional
    public UserDto.UserResponseDto updateUser(Long userId, UserDto.UserRequestDto requestDto, Long currentUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다"));

        // 본인 혹은 관리자만 수정 가능
        if (!userId.equals(currentUserId)) {
            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new UserNotFoundException("로그인한 사용자를 찾을 수 없습니다"));

            if (!currentUser.getRoles().contains(UserRole.ADMIN)) {
                throw new InsufficientPermissionException("다른 사용자의 정보를 수정할 권한이 없습니다");
            }
        }

        // 이메일 중복 체크 (변경된 경우에만)
        if (!user.getEmail().equals(requestDto.getEmail()) &&
                userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateValueException(ErrorCode.EMAIL_ALREADY_EXISTS, "이미 사용 중인 이메일입니다");
        }

        // 비밀번호 변경 시 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 사용자 정보 업데이트
        user = user.update(requestDto.getEmail(), encodedPassword, requestDto.getNickname());

        return UserDto.UserResponseDto.from(user);
    }

    /**
     * 사용자 포인트 충전
     */
    @Transactional
    public UserDto.UserResponseDto chargePoint(Long userId, UserDto.PointChargeRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다"));

        // 포인트 충전 (음수 체크)
        if (requestDto.getAmount() <= 0) {
            throw new BaseException(ErrorCode.INVALID_INPUT_VALUE, "충전 금액은 0보다 커야 합니다");
        }

        user.addPoint(requestDto.getAmount());

        return UserDto.UserResponseDto.from(user);
    }

    /**
     * 사용자 권한 추가
     */
    @Transactional
    public UserDto.UserResponseDto addRole(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다"));

        user.addRole(role);

        return UserDto.UserResponseDto.from(user);
    }

    /**
     * 사용자 권한 삭제
     */
    @Transactional
    public UserDto.UserResponseDto removeRole(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다"));

        // 기본 USER 권한은 삭제 불가
        if (role == UserRole.USER && user.getRoles().size() == 1) {
            throw new BaseException(ErrorCode.INVALID_INPUT_VALUE, "기본 USER 권한은 삭제할 수 없습니다");
        }

        user.removeRole(role);

        return UserDto.UserResponseDto.from(user);
    }

    /**
     * 사용자 활성화/비활성화
     */
    @Transactional
    public UserDto.UserResponseDto setUserEnabled(Long userId, boolean enable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다"));

        user.changeEnabled(enable);

        return UserDto.UserResponseDto.from(user);
    }

    /**
     * 닉네임으로 사용자 검색
     */
    public Page<UserDto.UserResponseDto> searchUsersByNickname(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BaseException(ErrorCode.INVALID_INPUT_VALUE, "검색 키워드는 비어있을 수 없습니다");
        }

        return userRepository.findByNicknameContaining(keyword, pageable)
                .map(UserDto.UserResponseDto::from);
    }

    /**
     * 특정 권한을 가진 사용자 목록 조회
     */
    public Page<UserDto.UserResponseDto> getUsersByRole(UserRole role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
                .map(UserDto.UserResponseDto::from);
    }

    /**
     * 사용자 필드 업데이트 헬퍼 메서드
     */
    private User updateUserFields(User user, UserDto.UserRequestDto requestDto, String encodedPassword) {
        // User 엔티티의 update 메서드 호출
        return user.update(requestDto.getEmail(), encodedPassword, requestDto.getNickname());
    }

    /**
     * 사용자 활성화 상태 변경 헬퍼 메서드
     */
    private User setUserEnabledState(User user, boolean enable) {
        // User 엔티티의 changeEnabled 메서드 호출
        return user.changeEnabled(enable);
    }
}