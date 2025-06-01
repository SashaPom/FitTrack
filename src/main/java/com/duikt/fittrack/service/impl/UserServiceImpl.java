package com.duikt.fittrack.service.impl;


import com.duikt.fittrack.domain.UserDTO;
import com.duikt.fittrack.entity.UserEntity;
import com.duikt.fittrack.exception.DuplicateUsernameException;
import com.duikt.fittrack.exception.UserNotFoundException;
import com.duikt.fittrack.mapper.UserMapper;
import com.duikt.fittrack.repository.UserRepository;
import com.duikt.fittrack.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        return userMapper.toUserDtoList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserDTO findUserById(UUID id) {
        return userMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id)));
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO, String password) {
        UserEntity newUser = userMapper.toUserEntity(userDTO, passwordEncoder.encode(password));

        return userMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    @Transactional
    public UserDTO updateUser(UUID id, UserDTO updatedUser) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (existByUsername(updatedUser.getUsername())
                && !updatedUser.getUsername().equals(existingUser.getUsername())) {
            throw new DuplicateUsernameException(updatedUser.getUsername());
        }
        UserDTO userWithUpdates = updatedUser.toBuilder()
                .id(id)
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .gender(updatedUser.getGender())
                .age(updatedUser.getAge())
                .weight(updatedUser.getWeight())
                .height(updatedUser.getHeight())
                .build();
        userRepository.save(userMapper.toUserEntity(userWithUpdates, passwordEncoder.encode(existingUser.getPassword())));
        return userWithUpdates;
    }

    @Override
    public UserDTO updatePassword(UUID id, String password, String passwordConf) throws UserNotFoundException {
        if (!password.equals(passwordConf)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        user.toBuilder()
                .password(passwordEncoder.encode(password))
                .build();
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserDTO findByEmail(String email) {
        return userMapper.toUserDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email)));
    }

    private boolean existByUsername(String username) {
        return userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }
}