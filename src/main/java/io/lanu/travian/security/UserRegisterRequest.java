package io.lanu.travian.security;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String email;
    private String username;
    private String password;
}
