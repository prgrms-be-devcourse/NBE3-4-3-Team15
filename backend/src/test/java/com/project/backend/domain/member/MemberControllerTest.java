package com.project.backend.domain.member;

import com.project.backend.domain.member.controller.MemberController;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

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
                                            "nickname": "테스트",
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
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.nickname").value(member.getNickname()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.gender").value(member.getGender()))
                .andExpect(jsonPath("$.birth").value(member.getBirth().toString()));
    }

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
                .andExpect(status().isConflict());
    }

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
}
