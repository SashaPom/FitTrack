package com.duikt.fittrack.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.duikt.fittrack.domain.UserDTO;
import com.duikt.fittrack.domain.enums.Role;
import com.duikt.fittrack.entity.UserEntity;
import com.duikt.fittrack.exception.UserNotFoundException;
import com.duikt.fittrack.mapper.UserMapper;
import com.duikt.fittrack.repository.UserRepository;
import com.duikt.fittrack.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    private UUID userId;
    private UserEntity userEntity;
    private UserDTO userDTO;
    private final String rawPassword = "rawpassword";
    private final String encodedPassword = "encodedpassword";

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder);
        userId = UUID.randomUUID();

        userEntity = UserEntity.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .password(encodedPassword)
                .role(Role.USER)
                .build();

        userDTO = UserDTO.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .build();
    }

    @Test
    void testFindAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(userEntity));
        when(userMapper.toUserDtoList(List.of(userEntity))).thenReturn(List.of(userDTO));

        List<UserDTO> result = userService.findAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(userRepository).findAll();
        verify(userMapper).toUserDtoList(List.of(userEntity));
    }

    @Test
    void testCreateUser() {
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userMapper.toUserEntity(userDTO, encodedPassword)).thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toUserDto(userEntity)).thenReturn(userDTO);

        UserDTO result = userService.createUser(userDTO, rawPassword);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(passwordEncoder).encode(rawPassword);
        verify(userMapper).toUserEntity(userDTO, encodedPassword);
        verify(userRepository).save(any(UserEntity.class));
        verify(userMapper).toUserDto(userEntity);
    }

    @Test
    void testFindUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toUserDto(userEntity)).thenReturn(userDTO);

        UserDTO result = userService.findUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository).findById(userId);
        verify(userMapper).toUserDto(userEntity);
    }

    @Test
    void testFindUserByIdNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(userId));
    }

    @Test
    void testFindByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
        when(userMapper.toUserDto(userEntity)).thenReturn(userDTO);

        UserDTO result = userService.findByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByEmail("test@example.com");
        verify(userMapper).toUserDto(userEntity);
    }

    @Test
    void testFindByEmailNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findByEmail("unknown@example.com"));
    }

    @Test
    void testUpdateUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        UserDTO updatedDTO = UserDTO.builder()
                .id(userId)
                .username("updateduser")
                .email("updated@example.com")
                .build();

        when(passwordEncoder.encode(userEntity.getPassword())).thenReturn(encodedPassword);
        UserEntity updatedEntity = UserEntity.builder()
                .id(userId)
                .username("updateduser")
                .email("updated@example.com")
                .password(encodedPassword)
                .role(Role.USER)
                .build();

        when(userMapper.toUserEntity(any(UserDTO.class), eq(encodedPassword))).thenReturn(updatedEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedEntity);

        UserDTO result = userService.updateUser(userId, updatedDTO);

        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode(userEntity.getPassword());
        verify(userMapper).toUserEntity(any(UserDTO.class), eq(encodedPassword));
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testDeleteUser() {
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void testExistsByEmail() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertTrue(userService.existsByEmail("test@example.com"));
        verify(userRepository).existsByEmail("test@example.com");
    }
}
