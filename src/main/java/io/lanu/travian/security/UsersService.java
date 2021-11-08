package io.lanu.travian.security;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService extends UserDetailsService{
    UserEntity registerUser(AuthRequest userDetails);
    UserEntity getUserByEmail(String email);
}
