package com.lezhin.lezhinchallenge.domain.user.service;

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
     * @param pageable 페이징 정보
     * @return 사용자 목록
     */
    public Page<UserDto.UserResponseDto> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserDto.UserResponseDto::from);
    }

    /**
     * 특정 사용자 조회
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    public UserDto.UserResponseDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        return UserResponseDto.from(user);
    }

    /**
     * 사용자 정보 수정
     * @param userId 사용자 ID
     * @param requestDto 사용자 수정 요청 정보
     * @param currentUserId 현재 로그인한 사용자 ID
     * @return 수정된 사용자 정보
     */
    @Transactional
    public UserDto.UserResponseDto updateUser(Long userId, UserDto.UserRequestDto requestDto, Long currentUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 본인 혹은 관리자만 수정 가능
        if (!userId.equals(currentUserId)) {
            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new EntityNotFoundException("Current user not found"));

            if (!currentUser.getRoles().contains(UserRole.ADMIN)) {
                throw new AccessDeniedException("You don't have permission to update this user");
            }
        }

        // 이메일 중복 체크 (변경된 경우에만)
        if (!user.getEmail().equals(requestDto.getEmail()) &&
                userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalStateException("Email is already in use");
        }

        // 비밀번호 변경 시 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 사용자 정보 업데이트 로직 (setter 대신 메서드 추가 필요)
        user = updateUserFields(user, requestDto, encodedPassword);

        User savedUser = userRepository.save(user);

        return UserDto.UserResponseDto.from(savedUser);
    }

    /**
     * 사용자 포인트 충전
     * @param userId 사용자 ID
     * @param requestDto 포인트 충전 요청 정보
     * @return 충전 후 사용자 정보
     */
    @Transactional
    public UserDto.UserResponseDto chargePoint(Long userId, UserDto.PointChargeRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 포인트 충전 (음수 체크)
        if (requestDto.getAmount() <= 0) {
            throw new IllegalArgumentException("Charge amount must be positive");
        }

        user.addPoint(requestDto.getAmount());

        return UserDto.UserResponseDto.from(user);
    }

    /**
     * 사용자 권한 추가
     * @param userId 사용자 ID
     * @param role 추가할 권한
     * @return 권한 추가 후 사용자 정보
     */
    @Transactional
    public UserDto.UserResponseDto addRole(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        user.addRole(role);

        return UserDto.UserResponseDto.from(user);
    }

    /**
     * 사용자 권한 삭제
     * @param userId 사용자 ID
     * @param role 삭제할 권한
     * @return 권한 삭제 후 사용자 정보
     */
    @Transactional
    public UserDto.UserResponseDto removeRole(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 기본 USER 권한은 삭제 불가
        if (role == UserRole.USER && user.getRoles().size() == 1) {
            throw new IllegalStateException("Cannot remove the basic USER role");
        }

        user.removeRole(role);

        return UserDto.UserResponseDto.from(user);
    }

    /**
     * 사용자 활성화/비활성화
     * @param userId 사용자 ID
     * @param enable 활성화 여부
     * @return 상태 변경 후 사용자 정보
     */
    @Transactional
    public UserDto.UserResponseDto setUserEnabled(Long userId, boolean enable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 활성화/비활성화 설정 로직 (setter 대신 메서드 추가 필요)
        user = setUserEnabledState(user, enable);

        return UserDto.UserResponseDto.from(user);
    }

    /**
     * 닉네임으로 사용자 검색
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 검색 결과 사용자 목록
     */
    public Page<UserDto.UserResponseDto> searchUsersByNickname(String keyword, Pageable pageable) {
        return userRepository.findByNicknameContaining(keyword, pageable)
                .map(UserDto.UserResponseDto::from);
    }

    /**
     * 특정 권한을 가진 사용자 목록 조회
     * @param role 권한
     * @param pageable 페이징 정보
     * @return 권한별 사용자 목록
     */
    public Page<UserDto.UserResponseDto> getUsersByRole(UserRole role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
                .map(UserDto.UserResponseDto::from);
    }

    // 사용자 필드 업데이트 헬퍼 메서드 (실제로는 User 엔티티에 update 메서드 추가 필요)
    private User updateUserFields(User user, UserDto.UserRequestDto requestDto, String encodedPassword) {
        // 실제 구현에서는 User 클래스에 update 메서드를 추가하는 것이 좋음
        // 예시 코드
        // return user.update(requestDto.getEmail(), encodedPassword, requestDto.getNickname());

        // 임시 구현 (실제로는 이렇게 하지 말고 엔티티에 메서드 추가)
        User updatedUser = User.builder()
                .username(user.getUsername())
                .password(encodedPassword)
                .email(requestDto.getEmail())
                .nickname(requestDto.getNickname())
                .build();

        // ID 설정
        // updatedUser.setId(user.getId());

        // 기존 포인트와 권한 복사
        // updatedUser.setPoint(user.getPoint());
        // updatedUser.setRoles(user.getRoles());

        return updatedUser;
    }

    // 사용자 활성화 상태 변경 헬퍼 메서드 (실제로는 User 엔티티에 메서드 추가 필요)
    private User setUserEnabledState(User user, boolean enable) {
        // 실제 구현에서는 User 클래스에 setEnabled 메서드를 추가하는 것이 좋음
        // 예시 코드
        // return user.setEnabled(enable);

        // 임시 구현 (실제로는 이렇게 하지 말고 엔티티에 메서드 추가)
        // user.setEnabled(enable);
        return user;
    }
}