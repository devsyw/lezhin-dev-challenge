package com.lezhin.lezhinchallenge.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDto {
    /**
     * 로그인 요청 DTO
     */
    @Data
    public static class LoginRequest {
        @NotBlank(message = "계정을 입력해주세요")
        private String username;

        @NotBlank(message = "비밀번호를 입력해주세요")
        private String password;
    }

    /**
     * 회원가입 요청 DTO
     */
    @Data
    public static class SignupRequest {
        @NotBlank(message = "계정을 입력해주세요")
        @Size(min = 3, max = 20, message = "계정의 길이는 3글자 이상, 20글자 이하로만 설정 가능합니다")
        private String username;

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Size(min = 6, max = 30, message = "비밀번호의 길이는 6글자 이상, 30글자 이하로만 설정 가능합니다.")
        private String password;

        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "이메일이 올바르지 않습니다")
        private String email;

        @Size(max = 20, message = "닉네임은 최대 20글자 입니다")
        private String nickname;
    }

    /**
     * JWT 응답 DTO
     */
    @Data
    public static class JwtResponse {
        private String token;
        private String type = "Bearer";

        public JwtResponse(String token) {
            this.token = token;
        }
    }
}
