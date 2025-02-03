package com.project.backend.domain.member.dto;

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
public class MemberDto extends MineDto {
    private long id;

    @NotBlank
    @Length(min = 2, max = 16)
    private String username;

    @NotBlank
    @Length(min = 8)
    private String password1;

    @NotBlank
    private String password2;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.gender = member.getGender();
        this.nickname = member.getNickname();
        this.birth = member.getBirth();
    }
}