package com.project.backend.domain.member;

import com.project.backend.domain.member.controller.MemberController;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 멤버 테스트
 *
 * author 손진영
 * since 2025.01.27
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    /**
     * 회원가입 테스트: 성공적인 회원가입을 검증
     *
     * @throws Exception
     * author 손진영
     * since 2025.01.27
     */
    @Test
    @DisplayName("회원가입")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/members")
                                .content("""
                                        {
                                            "id": "test2",
                                            "password1": "12345678",
                                            "password2": "12345678",
                                            "nickname": "테스트2",
                                            "email": "test@test.com",
                                            "gender" : "0",
                                            "birth" : "2024-10-12"
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print());

        Member member = memberService.getMember("test2").get();

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(member.getId()))
                .andExpect(jsonPath("$.data.nickname").value(member.getNickname()))
                .andExpect(jsonPath("$.data.email").value(member.getEmail()))
                .andExpect(jsonPath("$.data.gender").value(member.getGender()))
                .andExpect(jsonPath("$.data.birth").value(member.getBirth().toString()));
    }

    /**
     * 회원가입 시 중복된 아이디를 사용할 경우, 409 Conflict 상태 코드가 반환되는지 검증
     *
     * @throws Exception
     * author 손진영
     * since 2025.01.27
     */
    @Test
    @DisplayName("회원가입, 중복")
    void t2() throws Exception {
        mvc
                .perform(
                        post("/members")
                                .content("""    
                                        {
                                            "id": "test1",
                                            "password1": "12345678",
                                            "password2": "12345678",
                                            "nickname": "테스트1",
                                            "email": "test@test.com",
                                            "gender" : "0",
                                            "birth" : "2024-10-12"
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isConflict());
    }

    /**
     * 회원가입 시 입력 값이 유효하지 않으면, 400 Bad Request가 반환되는지 검증
     *
     * @throws Exception
     * author 손진영
     * since 2025.01.27
     */
    @Test
    @DisplayName("회원가입, valid")
    void t3() throws Exception {
        mvc
                .perform(
                        post("/members")
                                .content("""    
                                        {
                                            "id": "",
                                            "password1": "",
                                            "password2": "",
                                            "nickname": "",
                                            "email": "",
                                            "gender" : "",
                                            "birth" : ""
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isBadRequest());
    }

    /**
     * 회원가입 시 비밀번호가 일치하지 않으면, 400 Bad Request가 반환되는지 검증
     *
     * @throws Exception
     * author 손진영
     * since 2025.01.27
     */
    @Test
    @DisplayName("회원가입, 비밀번호 확인")
    void t4() throws Exception {
        mvc
                .perform(
                        post("/members")
                                .content("""    
                                        {
                                            "id": "test2",
                                            "password1": "123456789",
                                            "password2": "12345678",
                                            "nickname": "테스트",
                                            "email": "test@test.com",
                                            "gender" : "0",
                                            "birth" : "2024-10-12"
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isBadRequest());
    }

    /**
     * 회원가입 시 이메일 형식이 유효하지 않으면, 400 Bad Request가 반환되는지 검증
     *
     * @throws Exception
     * author 손진영
     * since 2025.01.27
     */
    @Test
    @DisplayName("회원가입, 이메일 형식")
    void t5() throws Exception {
        mvc
                .perform(
                        post("/members")
                                .content("""    
                                        {
                                            "id": "test2",
                                            "password1": "12345678",
                                            "password2": "12345678",
                                            "nickname": "테스트",
                                            "email": "test",
                                            "gender" : "0",
                                            "birth" : "2024-10-12"
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isBadRequest());
    }

    /**
     * 로그인 성공 시, 200 OK와 함께 로그인된 회원 정보를 반환하는지 검증
     *
     * @throws Exception
     * author 손진영
     * since 2025.01.27
     */
    @Test
    @DisplayName("로그인")
    void t6() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/members/login")
                                .content("""
                                        {
                                            "id" : "test1",
                                            "password" : "12345678"
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                );

        Member member = memberService.getMember("test1").get();

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.nickname").value(member.getNickname()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.gender").value(member.getGender()))
                .andExpect(jsonPath("$.birth").value(member.getBirth().toString()));
    }

    /**
     * 존재하지 않는 사용자가 로그인 시, 404 Not Found와 함께 "존재하지 않는 사용자 입니다." 메시지가 반환되는지 검증
     *
     * @throws Exception
     * author 손진영
     * since 2025.01.27
     */
    @Test
    @DisplayName("로그인, 없는 사용자")
    void t7() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/members/login")
                                .content("""
                                        {
                                            "id" : "test3",
                                            "password" : "12345678"
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                );

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("존재하지 않는 사용자 입니다."));
    }

    /**
     * 비밀번호가 맞지 않는 경우, 401 Unauthorized 상태 코드와 함께 "비밀번호가 맞지 않습니다." 메시지가 반환되는지 검증
     *
     * @throws Exception
     * author 손진영
     * since 2025.01.27
     */
    @Test
    @DisplayName("로그인, 비밀번호 맞지 않음")
    void t8() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/members/login")
                                .content("""
                                        {
                                            "id" : "test1",
                                            "password" : "12345678999"
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                );

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("비밀번호가 맞지 않습니다."));
    }
}
