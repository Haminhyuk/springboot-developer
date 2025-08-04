package org.zerock.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.zerock.springbootdeveloper.config.jwt.JwtFactory;
import org.zerock.springbootdeveloper.config.jwt.JwtProperties;
import org.zerock.springbootdeveloper.config.jwt.TokenProvider;
import org.zerock.springbootdeveloper.domain.RefreshToken;
import org.zerock.springbootdeveloper.domain.User;
import org.zerock.springbootdeveloper.dto.CreateAccessTokenRequest;
import org.zerock.springbootdeveloper.repository.RefreshTokenRepository;
import org.zerock.springbootdeveloper.repository.UserRepository;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void mockMvcSetup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        userRepository.deleteAll();
    }

    @DisplayName("createNewAccessToken: 새로운 엑세스 토큰을 발급한다.")
    @Test
    public void createNewAccessTokenTest() throws Exception{
        // 테스트 유저 생성
        final String url = "/api/token";

        User testUser = userRepository.save(User.builder()
                        .email("user@gmail.com")
                        .password("test")
                        .build());
        // 리프레시 토큰 생성

        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(jwtProperties);

        // 데이터베이스에 리프레시토큰을 저장
        refreshTokenRepository.save(new RefreshToken(testUser.getId(),refreshToken));

        // createNewAccessToken 메서드를 실행하기 위한 데이터를 생성
        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        // 위에서 만든 정상적인 RefeshToken을 requset에 저장
        request.setRefreshToken(refreshToken);
        final String requestBody = objectMapper.writeValueAsString(request);

        // TokenApiController의 createNewAccessToken 메서드를 실행
        ResultActions resultActions = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(requestBody));

        // 실행결과를 확인, 응답코드가 create면, accessToken이 있으면 통과
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

}
