package org.zerock.springbootdeveloper.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class CookieUtil {
    // 쿠키 생성 메서드
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        // 쿠키의 키와 값을 설정하여 쿠키 생성
        Cookie cookie = new Cookie(name, value);
        // 쿠키의 사용경로 설정
        cookie.setMaxAge(maxAge);
        // 쿠키의 사용 기한 설정
        cookie.setPath("/");
        // 쿠키를 응답(Response)에 저장
        response.addCookie(cookie);
    }
    // 쿠키 삭제 메서드
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        // 모든 쿠키를 쿠키 배열에 저장
        Cookie[] cookies = request.getCookies();
        // 쿠키가 없으면 메서드를 종료
        if (cookies == null) {
            return;
        }
        // 반복문을 이용하여 모든 쿠키를 체크
        for (Cookie cookie : cookies) {
            // 삭제할 쿠키의 이름과 반복하고 있는 쿠키의 이름이 일치하면 실행하는 if문
            if (name.equals(cookie.getName())) {
                // 쿠키의 내용을 빈칸으로 설정
                cookie.setValue("");
                // 패스를 초기화
                cookie.setPath("/");
                // 기한을 0으로 설정 => 쿠키는 삭제가 아닌 기한을 0으로 만드는 처리로 삭제처럼 사용
                cookie.setMaxAge(0);
                // 기한이 0이된 쿠키를 응답에 저장
                response.addCookie(cookie);
            }
        }
    }
    // 직렬화 : 쿠키에 저장할 객체를 네트워크 통신에서 사용할 수 있도록 문자열로 변환
    // 쿠키는 문자열만 저장할 수 있음
    public static String serialize(Object obj) {
        return Base64.getEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    // 역직렬화 : 자바에서 사용할 수 있도록 문자열로 변환된 데이터를 원래의 객체로 변환
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(Base64.getDecoder().decode(cookie.getValue())));
    }
}
