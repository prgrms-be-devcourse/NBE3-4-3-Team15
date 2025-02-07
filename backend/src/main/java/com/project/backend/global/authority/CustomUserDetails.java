package com.project.backend.global.authority;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * 스프링시큐리티의 유저정보를 가져오는 클래스
 * UserDetails을 상속받아 유저의 id값도 가져올 수 있게 한다
 *
 * @author -- 정재익 --
 * @since -- 2월 3일 --
 */
public class CustomUserDetails implements UserDetails, OAuth2User {
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String username, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public String getName() {
        return "";
    }
}
