package com.project.backend.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.domain.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 *
 * 회원 DTO
 *
 * id, username, password1, password2, password, email, nickname, gender, birth
 * @author 손진영
 * @since 25. 1. 27.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberDto extends MineDto {
    @JsonProperty
    private long id;

    @NotBlank
    @Length(min = 2, max = 16)
    private String username;

    @JsonProperty
    @NotBlank
    @Length(min = 8)
    private String password1;

    @JsonProperty
    @NotBlank
    private String password2;

    // 팔로워 수와 팔로잉 수 필드 추가
    private Long followerCount;
    private Long followingCount;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.gender = member.getGender();
        this.nickname = member.getNickname();
        this.birth = member.getBirth();
    }

    // 팔로잉, 팔로워 카운트 설정 메서드 추가
    public void setFollowCounts(Long followerCount, Long followingCount) {
        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }

    public Long getId(){
        return id;
    }
}
