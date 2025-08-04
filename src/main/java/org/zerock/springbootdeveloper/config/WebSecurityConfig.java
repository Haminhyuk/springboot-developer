//package org.zerock.springbootdeveloper.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
////import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
////import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.security.web.SecurityFilterChain;
//import org.zerock.springbootdeveloper.repository.UserRepository;
//import org.zerock.springbootdeveloper.service.UserDetailService;
//
//import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class WebSecurityConfig {
//
//    private final UserRepository userService;
//
////    web.ignoring() : 스프링 시큐리티를 적용하지 않을 주소들을 설정
//    @Bean
//    public WebSecurityCustomizer configure() {
//        return (web -> web.ignoring()
//                // /h2-console의 접속에 스프링 시큐리티를 해제
//                .requestMatchers(toH2Console())
//                // resource 폴더의 static 폴더 접속에 스프링 시큐리티를 해제
//                //.requestMatchers(new AntPathRequestMatcher("/static/**")); 와 같음
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()));
//    }
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        // requestMatchers(허용하고 싶은 Controller에서 사용할 주소).permitAll
//        http.authorizeHttpRequests(authorize ->
//                        authorize.requestMatchers("login","/signup","/user","/articles")
//                .permitAll()
//                // 위에서 허용한 주소 이외에는 모두 로그인이 필요하도록 설정
//                .anyRequest().authenticated())
//                // 로그인 관련 설정
//                .formLogin(formLogin
//                                // /login 주소로 로그인 페이지를 설정
//                                -> formLogin.loginPage("/login")
//                                 // 로그인 성공시 출력할 페이지
//                                 .defaultSuccessUrl("/articles")
//                        )
//                // 로그아웃 관련 설정
//                .logout(logout
//                            // 로그아웃 성공시 실행할 페이지
//                        -> logout.logoutSuccessUrl("/login")
//                        // 세션의 모든 데이터를 삭제
//                        .invalidateHttpSession(true)
//                )
//                // csrf 비활성화
//                // CROSS-SITE REQUEST FORGERY
//                // POST, PUT , DELETE 요청이 외부에서 위조되어 들어오는것을 막는 기능
//                // 페이지를 보낼때 csrf용 토큰을 함께 전달하고 POST, PUT, DELETE 요청시 토큰이 없으면 실행되지 않도록하여 외부접속을 막는 방식
//                .csrf(AbstractHttpConfigurer::disable);
//
//        return http.build();
//    }
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http,
//                                                       BCryptPasswordEncoder bCryptPasswordEncoder,
//                                                       UserDetailService userDetailService) throws Exception {
//        // 인증 관리자 설정
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        // 사용자 정보를 가지고올 방식 설정 => h2 데이터베이스에서 User를 가지고 옴
//        authProvider.setUserDetailsService(userDetailService);
//        // 비밀번호 암호화 인코더 설정
//        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
//
//        return new ProviderManager(authProvider);
//    }
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
