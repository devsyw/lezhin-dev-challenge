package com.lezhin.lezhinchallenge.domain.auth.controller;


import com.lezhin.lezhinchallenge.common.config.JwtTokenUtil;
import com.lezhin.lezhinchallenge.domain.auth.dto.AuthDto;
import com.lezhin.lezhinchallenge.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 인증 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthService authService;

    /**
     * 로그인 처리
     * @param loginRequest
     * @return JWT 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<AuthDto.JwtResponse> login(@Valid @RequestBody AuthDto.LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtTokenUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthDto.JwtResponse(jwt));
    }

    /**
     * 회원가입 처리
     * @param signupRequest
     * @return 회원가입 결과
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody AuthDto.SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok("회원가입 되셨습니다.");
    }
}