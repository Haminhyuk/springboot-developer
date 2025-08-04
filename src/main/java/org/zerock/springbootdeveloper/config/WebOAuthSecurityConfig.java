package org.zerock.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.zerock.springbootdeveloper.config.jwt.TokenProvider;
import org.zerock.springbootdeveloper.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import org.zerock.springbootdeveloper.config.oauth.OAuth2SuccessHandler;
import org.zerock.springbootdeveloper.config.oauth.OAuth2UserCustomService;
import org.zerock.springbootdeveloper.repository.RefreshTokenRepository;
import org.zerock.springbootdeveloper.repository.UserRepository;
import org.zerock.springbootdeveloper.service.UserDetailService;
import org.zerock.springbootdeveloper.service.UserService;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {

    private final UserService userService;
    private final OAuth2UserCustomService  oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository  refreshTokenRepository;

    //web.ignoring() : 스프링 시큐리티를 적용하지 않을 주소들을 설정
    @Bean
    public WebSecurityCustomizer configure() {
        return (web -> web.ignoring()
                // /h2-console의 접속에 스프링 시큐리티를 해제
//                .requestMatchers(toH2Console())
                // resource 폴더의 static 폴더 접속에 스프링 시큐리티를 해제
                //.requestMatchers(new AntPathRequestMatcher("/static/**")); 와 같음
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()));
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // csrf 설정 끄기
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                // 세션을 사용하지 않도록 설정
                .sessionManagement(management ->
                        management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // addFilterBefore(필터1, 필터2)
                // 필터2가 실행되기 전 필터1을 실행하도록하는 메서드
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // 권한 없이 실행 가능
                        .requestMatchers("/api/token").permitAll()
                        // jwt 인증 필요
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                        .oauth2Login(oauth2 -> oauth2.loginPage("/login")
                                // 로그인 처리를 실행할 서비스 설정
                                .authorizationEndpoint(authorizationEndpoint ->
                                authorizationEndpoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oAuth2UserCustomService))
                                // 로그인을 설공했을때 실행할 핸들러 설정
                                .successHandler(oAuth2SuccessHandler())
                )
                // 예외처리 설정
                .exceptionHandling(exceptionHandling -> exceptionHandling.defaultAuthenticationEntryPointFor(
                        // /api/** 실행시 예외가 발생하면 UNAUTHORIZED(401)번 코드를 반환
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**"))

                )
                .build();
    }
    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider, refreshTokenRepository, oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() throws Exception {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
