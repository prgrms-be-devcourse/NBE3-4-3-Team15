package com.project.backend.domain.challenge.challenge.controller;

import com.project.backend.domain.challenge.challenge.entity.Challenge;
import com.project.backend.domain.challenge.challenge.service.ChallengeService;
import com.project.backend.global.jwt.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser
public class ChallengeControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 회원가입 테스트: 성공적인 회원가입을 검증
     *
     * @throws Exception
     * @author 손진영
     * @since 2025.01.27
     */
    @Test
    @DisplayName("챌린지 생성")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/challenge/create")
                                .content("""
                                        {
                                            "name": "ahahah",
                                            "content": "14141",
                                            "startDate": "2025-03-06T00:44:11.064Z",
                                            "endDate": "2025-03-06T00:44:11.064Z"
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print());

        Challenge challenge = challengeService.findLatest().get();

        resultActions
                .andExpect(handler().handlerType(ChallengeController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(challenge.getName()))
                .andExpect(jsonPath("$.data.content").value(challenge.getContent()))
                .andExpect(jsonPath("$.data.startDate").value(challenge.getStartDate().toString()))
                .andExpect(jsonPath("$.data.endDate").value(challenge.getEndDate().toString()))
                .andExpect(jsonPath("$.data.status").value(challenge.getStatus().toString()))
                .andExpect(jsonPath("$.data.totalDeposit").value(challenge.getTotalDeposit()));
    }

    @Test
    @DisplayName("챌린지 생성, valid")
    void t2() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/challenge/create")
                                .content("""
                                        {
                                            "name": "",
                                            "content": "",
                                            "startDate": "",
                                            "endDate": ""
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ChallengeController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("챌린지 참가")
    @WithUserDetails("user1")
    void t3() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/challenge/4/join")
                                .content("""
                                        {
                                            "deposit": 1000000
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print());

        Challenge challenge = challengeService.getChallenge(4);

        resultActions
                .andExpect(handler().handlerType(ChallengeController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(challenge.getName()))
                .andExpect(jsonPath("$.data.content").value(challenge.getContent()))
                .andExpect(jsonPath("$.data.startDate").value(challenge.getStartDate().toString()))
                .andExpect(jsonPath("$.data.endDate").value(challenge.getEndDate().toString()))
                .andExpect(jsonPath("$.data.status").value(challenge.getStatus().toString()))
                .andExpect(jsonPath("$.data.totalDeposit").value(challenge.getTotalDeposit()));
    }

    @Test
    @DisplayName("챌린지 참가, 중복참가")
    @WithUserDetails("user1")
    void t4() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/challenge/1/join")
                                .content("""
                                        {
                                            "deposit": 1000000
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ChallengeController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("챌린지 참가, valid")
    @WithUserDetails("user1")
    void t5() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/challenge/1/join")
                                .content("""
                                        {
                                            "deposit":
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ChallengeController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("챌린지 인증")
    @WithUserDetails("user1")
    void t6() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/challenge/1/validation")
                )
                .andDo(print());

        Challenge challenge = challengeService.getChallenge(1);

        resultActions
                .andExpect(handler().handlerType(ChallengeController.class))
                .andExpect(handler().methodName("validation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(challenge.getName()))
                .andExpect(jsonPath("$.data.content").value(challenge.getContent()))
                .andExpect(jsonPath("$.data.startDate").value(challenge.getStartDate().toString()))
                .andExpect(jsonPath("$.data.endDate").value(challenge.getEndDate().toString()))
                .andExpect(jsonPath("$.data.status").value(challenge.getStatus().toString()))
                .andExpect(jsonPath("$.data.totalDeposit").value(challenge.getTotalDeposit()));
    }

    @Test
    @DisplayName("챌린지 인증 X")
    @WithUserDetails("user2")
    void t7() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/challenge/3/validation")
                )
                .andDo(print());

        Challenge challenge = challengeService.getChallenge(3);

        resultActions
                .andExpect(handler().handlerType(ChallengeController.class))
                .andExpect(handler().methodName("validation"))
                .andExpect(status().isBadRequest());
    }
}
