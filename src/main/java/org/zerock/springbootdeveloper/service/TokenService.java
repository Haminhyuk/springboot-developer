package org.zerock.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.springbootdeveloper.config.jwt.TokenProvider;
import org.zerock.springbootdeveloper.domain.User;
import org.zerock.springbootdeveloper.repository.RefreshTokenRepository;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public String createNewAccessToken(String refreshToken){
        // RefreshToken이 만료되었을 경우 에러 발생
        if(!tokenProvider.validToken(refreshToken)){
           throw new IllegalArgumentException("Unexpected token");
        }
        // RefreshToken이 정상이라면 UserId를 저장
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        // UserId로 User데이터를 저장
        User user = userService.findById(userId);
        // 새로운 엑세스 토큰을 만들어서 반환
        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
