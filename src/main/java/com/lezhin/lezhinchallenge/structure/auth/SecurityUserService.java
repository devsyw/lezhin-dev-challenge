package com.lezhin.lezhinchallenge.structure.auth;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 사용자 인증정보 로드
 */
@Service
public class SecurityUserService implements UserDetailsService {

    /**
     * 사용자명으로 사용자 정보 조회
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: 엔티티 구현 후 숮정
        String role = "admin".equals(username) ? "ROLE_ADMIN" : "ROLE_USER";
        return new User("", "",
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}