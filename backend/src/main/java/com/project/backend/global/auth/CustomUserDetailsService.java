package com.project.backend.global.auth;

import com.project.backend.domain.member.repository.MemberRepository;
import com.project.backend.global.authority.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * -- 사용자 상세 정보를 로드하는 서비스 --
 * 이 클래스는 스프링 시큐리티의 UserDetailsService 인터페이스를 구현하여
 * 데이터베이스에서 사용자 정보를 조회하고, 이를 기반으로 인증을 처리하는 역할을 한다.
 *
 * @author 이원재
 * @since 25. 2. 4.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    /**
     * 사용자 이름(username)을 기반으로 사용자 정보를 로드하여 UserDetails 객체로 반환
     *
     * @param username 사용자 이름
     * @return UserDetails - 인증에 필요한 사용자 정보
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 예외 발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
                .map(member -> new CustomUserDetails(
                        member.getUsername(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("username:" + username + " / 해당 사용자를 찾을 수 없습니다."));
    }
}