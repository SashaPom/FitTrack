package com.duikt.fittrack.service.impl;

import com.duikt.fittrack.config.security.jwt.JwtProvider;
import com.duikt.fittrack.domain.UserDTO;
import com.duikt.fittrack.domain.auth.LoginDTO;
import com.duikt.fittrack.domain.auth.RegisterDTO;
import com.duikt.fittrack.domain.enums.Role;
import com.duikt.fittrack.entity.UserEntity;
import com.duikt.fittrack.exception.AuthenticationException;
import com.duikt.fittrack.exception.UserAlreadyExistsException;
import com.duikt.fittrack.repository.UserRepository;
import com.duikt.fittrack.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO registerUser(RegisterDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        UserEntity user = userRepository.save(UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build());

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    @Override
    public UserDTO loginUser(LoginDTO request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    public String generateToken(UserDTO user) {
        Role role = userRepository.findByEmail(user.getEmail()).get().getRole();
        return jwtProvider.createToken(user.getEmail(), role);
    }
}
