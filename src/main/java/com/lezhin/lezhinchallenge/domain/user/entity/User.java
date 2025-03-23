package com.lezhin.lezhinchallenge.domain.user.entity;

import com.lezhin.lezhinchallenge.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String nickname;

    @Column(nullable = false)
    private int point;  // 포인트(만화 구매에 사용)

    private boolean accountNonExpired = true; // TODO (계정만료) 추가할거면 날짜 필드추가, 보류
    private boolean accountNonLocked = true; // 잠금해제
    private boolean credentialsNonExpired = true; // 비번만료
    private boolean enabled = true; // 계정활성

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<UserRole> roles = new HashSet<>();

    @Builder
    public User(String username, String password, String email, String nickname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.point = 0;
    }

    /**
     * 포인트 추가
     */
    public void addPoint(int amount) {
        this.point += amount;
    }

    /**
     * 포인트 사용
     */
    public boolean usePoint(int amount) {
        if (this.point >= amount) {
            this.point -= amount;
            return true;
        }
        return false;
    }
    public void addRole(UserRole role) {
        this.roles.add(role);
    }

    public void removeRole(UserRole role) {
        this.roles.remove(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 사용자 정보 업데이트
     */
    public User update(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        return this;
    }

    /**
     * 사용자 활성화 상태 변경
     */
    public User changeEnabled(boolean enabled) {
        this.enabled = enabled;
        this.accountNonExpired = enabled;
        this.accountNonLocked = enabled;
        this.credentialsNonExpired = enabled;
        return this;
    }
}