package com.lezhin.lezhinchallenge.domain.auth.service;


import com.lezhin.lezhinchallenge.common.config.JwtTokenUtil;
import com.lezhin.lezhinchallenge.domain.auth.dto.AuthDto;
import com.lezhin.lezhinchallenge.domain.user.entity.User;
import com.lezhin.lezhinchallenge.domain.user.entity.UserRole;
import com.lezhin.lezhinchallenge.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
     * 회원가입
     */
    @Transactional
    public void signup(AuthDto.@Valid SignupRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("이미 존재하는 계정입니다");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일 입니다.");
        }

        // 사용자 생성
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .nickname(request.getNickname())
                .build();
        user.addRole(UserRole.USER);

        userRepository.save(user);
    }

    /**
     * 로그인 처리
     * @param loginRequest 로그인 요청 정보
     * @return JWT 토큰 응답
     */
    public AuthDto.JwtResponse login(AuthDto.LoginRequest loginRequest) {
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
    }
}