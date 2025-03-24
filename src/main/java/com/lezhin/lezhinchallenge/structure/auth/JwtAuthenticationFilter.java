package com.lezhin.lezhinchallenge.structure.auth;

import com.lezhin.lezhinchallenge.common.config.JwtTokenUtil;
import com.lezhin.lezhinchallenge.common.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * JWT 필터
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final SecurityUserService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // JWT 토큰은 "Bearer token" 형식으로 전달됨
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                log.error("JWT 토큰을 가져올 수 없습니다");
            } catch (ExpiredJwtException e) {
                log.error("JWT 토큰이 만료되었습니다");
                setErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
                return;
            } catch (SignatureException e) {
                log.error("JWT 토큰 서명이 유효하지 않습니다");
                setErrorResponse(response, ErrorCode.INVALID_TOKEN);
                return;
            } catch (MalformedJwtException e) {
                log.error("JWT 토큰 형식이 잘못되었습니다");
                setErrorResponse(response, ErrorCode.INVALID_TOKEN);
                return;
            } catch (UnsupportedJwtException e) {
                log.error("지원하지 않는 JWT 토큰입니다");
                setErrorResponse(response, ErrorCode.INVALID_TOKEN);
                return;
            }
        } else {
            log.debug("JWT 토큰이 없거나 Bearer로 시작하지 않습니다");
        }

        // 토큰 검증 및 인증 정보 설정
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                setErrorResponse(response, ErrorCode.INVALID_TOKEN);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * 에러 응답 설정
     */
    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

        String jsonResponse = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"code\":\"%s\",\"message\":\"%s\"}",
                java.time.LocalDateTime.now().format(formatter),
                errorCode.getHttpStatus().value(),
                errorCode.getHttpStatus().name(),
                errorCode.getCode(),
                errorCode.getMessage()
        );

        response.getWriter().write(jsonResponse);
    }
}