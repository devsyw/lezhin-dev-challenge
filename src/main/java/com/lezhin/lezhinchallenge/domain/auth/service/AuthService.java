package com.lezhin.lezhinchallenge.domain.auth.service;


import com.lezhin.lezhinchallenge.common.config.JwtTokenUtil;
import com.lezhin.lezhinchallenge.common.exception.BaseException;
import com.lezhin.lezhinchallenge.common.exception.ErrorCode;
import com.lezhin.lezhinchallenge.domain.auth.dto.AuthDto;
import com.lezhin.lezhinchallenge.domain.user.entity.User;
import com.lezhin.lezhinchallenge.domain.user.entity.UserRole;
import com.lezhin.lezhinchallenge.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 회원가입 처리
     */
    @Transactional
    public void signup(AuthDto.SignupRequest request) {
        // 사용자명 중복 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BaseException(ErrorCode.USERNAME_ALREADY_EXISTS, "이미 사용 중인 사용자명입니다");
        }

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BaseException(ErrorCode.EMAIL_ALREADY_EXISTS, "이미 사용 중인 이메일입니다");
        }

        // 사용자 생성
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .nickname(request.getNickname())
                .build();

        // 기본 권한 설정
        user.addRole(UserRole.USER);

        // 사용자 저장
        userRepository.save(user);
    }

    /**
     * 로그인 처리
     */
    public AuthDto.JwtResponse login(AuthDto.LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtTokenUtil.generateToken(userDetails.getUsername());

            return new AuthDto.JwtResponse(jwt);
        } catch (DisabledException e) {
            throw new BaseException(ErrorCode.ACCOUNT_DISABLED, "계정이 비활성화되었습니다");
        } catch (BadCredentialsException e) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD, "아이디 또는 비밀번호가 올바르지 않습니다");
        } catch (AuthenticationException e) {
            throw new BaseException(ErrorCode.UNAUTHORIZED, "인증에 실패했습니다: " + e.getMessage());
        }
    }
}