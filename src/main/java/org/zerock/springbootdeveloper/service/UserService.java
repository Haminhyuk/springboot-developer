package org.zerock.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.springbootdeveloper.domain.User;
import org.zerock.springbootdeveloper.dto.AddUserRequest;
import org.zerock.springbootdeveloper.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;


    public Long save(AddUserRequest dto) throws Exception {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.save(User.builder()
                .email(dto.getEmail())
                // 비밀번호 저장시 암호화를 하여 저장, 암호화 하지 않으면 로그인 불가
                .password(encoder.encode(dto.getPassword()))
                .build()).getId();
    }

    // 자동으로 생성되는 1씩 더해지는 user_id로 데이터를 검색
    // Token에 저장하는 값이 user_id이기 때문에 작성하는 메서드
    public User findById(Long Userid){
        return userRepository.findById(Userid)
                .orElseThrow(()-> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("Unexpected user"));
    }


}
