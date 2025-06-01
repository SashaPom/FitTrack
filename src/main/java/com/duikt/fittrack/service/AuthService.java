package com.duikt.fittrack.service;


import com.duikt.fittrack.domain.UserDTO;
import com.duikt.fittrack.domain.auth.LoginDTO;
import com.duikt.fittrack.domain.auth.RegisterDTO;

public interface AuthService {
    UserDTO loginUser(LoginDTO request);

    UserDTO registerUser(RegisterDTO request);
    String generateToken(UserDTO user);
}
