package com.project.backend.global.oauth;

import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * OAuth2 사용자 정보를 기반으로 회원 정보를 조회하거나 생성하는 서비스 클래스
 *
 * @author 손진영
 * @since 25. 02. 07.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";
    private static final String GOOGLE = "google";

    private final MemberRepository memberRepository;

    /**
     * OAuth2 사용자 정보를 로드하고 사용자 정보를 기반으로 회원을 생성 또는 조회
     *
     * @param userRequest OAuth2 사용자 요청 객체
     * @return 사용자 정보 객체
     * @throws OAuth2AuthenticationException 인증 예외 발생 시
     * @author 손진영
     * @since 25. 02. 07.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = getAttributes(registrationId, oAuth2User.getAttributes());

        String username = attributes.get("username").toString();

        Optional<Member> opMember = memberRepository.findByUsername(username);

        Member member = opMember.orElseGet(() -> saveMember(attributes, registrationId));

        return new CustomOAuth2UserDetails(
                member.getUsername(),
                attributes,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    /**
     * 각 소셜 마다 attributes 재맵핑
     *
     * @param registrationId
     * @param attributes
     * @return Map<String, Object>
     * @author 손진영
     * @since 25. 02. 07.
     */
    private static Map<String, Object> getAttributes(String registrationId, Map<String, Object> attributes) {
        if (NAVER.equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            response.put("username", response.get("email"));
            attributes = response;
        } else if (KAKAO.equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            profile.put("username", attributes.get("id"));

            attributes = profile;
        } else if (GOOGLE.equals(registrationId)) {
            Map<String, Object> googleMap = new HashMap<>();
            googleMap.put("username", attributes.get("email"));
            googleMap.put("email", attributes.get("email"));
            googleMap.put("nickname", attributes.get("name"));

            attributes = googleMap;
        }
        return attributes;
    }

    /**
     * 소셜 로그인 DB 저장
     *
     * @param attributes
     * @param registrationId
     * @return Member
     * @author 손진영
     * @since 25. 02. 07.
     */
    private Member saveMember(Map<String, Object> attributes, String registrationId) {
        return memberRepository.save(Member.builder()
                .username(attributes.get("username").toString())
                .email("kakao".equals(registrationId) ? "user@daum.net" : attributes.get("email").toString())
                .nickname(attributes.get("nickname").toString())
                .build());
    }
}
