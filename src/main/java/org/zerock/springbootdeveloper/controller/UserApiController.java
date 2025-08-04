package org.zerock.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.zerock.springbootdeveloper.dto.AddUserRequest;
import org.zerock.springbootdeveloper.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserApiController {
    private final UserService userService;

    @PostMapping("/user")
    public String signup(AddUserRequest request) throws Exception {
        userService.save(request);
        return "redirect:/login";
    }
}
