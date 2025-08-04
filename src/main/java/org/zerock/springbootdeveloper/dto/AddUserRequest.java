package org.zerock.springbootdeveloper.dto;

import lombok.Data;

@Data
public class AddUserRequest {
    private String email;
    private String password;
}
