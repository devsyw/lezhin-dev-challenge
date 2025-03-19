package com.lezhin.lezhinchallenge.domain.user;

public enum UserRole {
    ADMIN,      // 관리자(시스템관리자)
    USER,       // 일반 사용자
    CREATOR,    // 콘텐츠 제작자
    EDITOR,     // 편집자(컨텐츠관리자)
    PREMIUM;    // 프리미엄 사용자

    @Override
    public String toString() {
        return this.name();
    }

    public String getAuthority() {
        return "ROLE_" + name();
    }
}