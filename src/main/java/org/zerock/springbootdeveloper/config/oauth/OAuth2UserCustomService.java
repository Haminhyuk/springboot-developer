package org.zerock.springbootdeveloper.config.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.zerock.springbootdeveloper.domain.User;
import org.zerock.springbootdeveloper.repository.UserRepository;

import java.util.Map;

@RequiredArgsConstructor
@Service
// OAuth2용 로그인 객체를 저장하는 Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        saveOrUpdate(user);
        return user;
    }

    private User saveOrUpdate(OAuth2User oAuth2User){
        // 구글 로그인시 보내주는 데이터가 들어있음
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        // User 테이블에 회원정보가 있으면 upadte만 실행
        // 회원정보가 없는 경우 새로운 회원을 생성
        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name))
                .orElse(User.builder()
                        .email(email)
                        .nickname(name)
                        .build());
        // 데이터베이스에 결과를 저장
        // 유저가 있으면 업데이트, 유저가 없으면 생성해서 저장
        return userRepository.save(user);
    }

}
