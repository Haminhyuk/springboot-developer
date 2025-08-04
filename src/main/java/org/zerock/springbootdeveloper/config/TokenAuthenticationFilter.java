package org.zerock.springbootdeveloper.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerock.springbootdeveloper.config.jwt.TokenProvider;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    // 토큰을 헤더에 담아서 보낼때의 키
    private final static String HEADER_AUTHORIZATION = "Authorization";
    // JWT 앞에 고정적으로 들어가는 문구
    private final static String TOKEN_PREFIX = "Bearer ";


    @Override
    // 필터 : 요청이 실행되기전 실행되는 메서드
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더 데이터를 저장 -> 토큰
        String authorization = request.getHeader(HEADER_AUTHORIZATION);
        // Bearer 문구를 때고 뒤의 토큰데이터만 변수에 저장
        String token = getAccessToken(authorization);
        // 토큰이 정상적인지, 기간이 만료되지는 않았는지 확인해주는 if문
        if (tokenProvider.validToken(token)) {
            // Token에 있는 데이터로 SpringSecurity 로그인 정보를 생성
            Authentication authentication = tokenProvider.getAuthentication(token);
            // 로그인 정보를 SpringSecurity 로그인 저장소에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 다음 필터가 있다면 실행 없다면 요청했던 내용을 실행
        // 다음 처리를 실행
        filterChain.doFilter(request, response);

    }

    private String getAccessToken(String authorizationHeader) {
        // JWT는 토큰 앞에 Bearer라는 문구를 가지고 있음
        // Bearer문구를 때고 토큰만 저장하도록 만든 메서드
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
