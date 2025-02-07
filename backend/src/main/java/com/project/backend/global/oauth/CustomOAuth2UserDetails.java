package com.project.backend.global.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * 사용자 정보를 커스터마이징하여 저장하기 위한 OAuth2User 구현 클래스
 *
 * @author 손진영
 * @since 25. 02. 07.
 */
public class CustomOAuth2UserDetails implements OAuth2User {
    private final String username;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOAuth2UserDetails(String username, Map<String, Object> attributes, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.attributes = attributes;
        this.authorities = authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return username;
    }
}
