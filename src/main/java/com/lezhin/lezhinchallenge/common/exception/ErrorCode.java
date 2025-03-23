package com.lezhin.lezhinchallenge.common.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 에러 코드
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통 오류
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "지원하지 않는 HTTP 메서드입니다"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 리소스를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 내부 오류가 발생했습니다"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "잘못된 타입의 값입니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "접근 권한이 없습니다"),

    // 사용자 관련 오류
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "존재하지 않는 사용자입니다"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "이미 사용 중인 사용자명입니다"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "U003", "이미 사용 중인 이메일입니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U004", "유효하지 않은 비밀번호입니다"),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "U005", "비활성화된 계정입니다"),

    // 인증/인가 관련 오류
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 토큰입니다"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다"),
    INSUFFICIENT_PERMISSION(HttpStatus.FORBIDDEN, "A004", "권한이 부족합니다"),

    // 작품 관련 오류
    WORK_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "존재하지 않는 작품입니다"),
    EPISODE_NOT_FOUND(HttpStatus.NOT_FOUND, "W002", "존재하지 않는 에피소드입니다"),
    DUPLICATE_EPISODE_NUMBER(HttpStatus.CONFLICT, "W003", "이미 존재하는 에피소드 번호입니다"),

    // 구매 관련 오류
    ALREADY_PURCHASED(HttpStatus.CONFLICT, "P001", "이미 구매한 작품입니다"),
    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "P002", "포인트가 부족합니다"),
    PURCHASE_NOT_FOUND(HttpStatus.NOT_FOUND, "P003", "존재하지 않는 구매 내역입니다"),

    // 조회 이력 관련 오류
    HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "H001", "존재하지 않는 조회 이력입니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}