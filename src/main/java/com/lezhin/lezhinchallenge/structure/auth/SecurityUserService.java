package com.lezhin.lezhinchallenge.structure.auth;


import com.lezhin.lezhinchallenge.common.exception.ErrorCode;
import com.lezhin.lezhinchallenge.common.exception.custom.UserNotFoundException;
import com.lezhin.lezhinchallenge.domain.user.entity.User;
import com.lezhin.lezhinchallenge.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 사용자 인증정보 로드
 */
@Service
@RequiredArgsConstructor
public class SecurityUserService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자명으로 사용자 정보 조회
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자명이 " + username + "인 사용자를 찾을 수 없습니다"));

        // 계정이 비활성화된 경우 사용 불가
        if (!user.isEnabled()) {
            throw new com.lezhin.lezhinchallenge.common.exception.custom.AuthenticationException(
                    ErrorCode.ACCOUNT_DISABLED, "계정이 비활성화되었습니다");
        }

        return user;
    }
}