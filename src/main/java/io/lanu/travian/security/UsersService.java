package io.lanu.travian.security;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService extends UserDetailsService{
    UserEntity registerUser(UserRegisterRequest userDetails);
    UserEntity getUserByEmail(String email);
}
