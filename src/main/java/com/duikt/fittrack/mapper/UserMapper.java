package com.duikt.fittrack.mapper;

import com.duikt.fittrack.domain.UserDTO;
import com.duikt.fittrack.domain.enums.Role;
import com.duikt.fittrack.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDTO toUserDto(UserEntity entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .gender(entity.getGender())
                .age(entity.getAge())
                .weight(entity.getWeight())
                .height(entity.getHeight())
                .build();
    }

    public UserEntity toUserEntity(UserDTO dto, String encodedPassword) {
        return UserEntity.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(encodedPassword)
                .role(Role.USER)
                .gender(dto.getGender())
                .age(dto.getAge())
                .weight(dto.getWeight())
                .height(dto.getHeight())
                .build();
    }

    public List<UserDTO> toUserDtoList(List<UserEntity> entities) {
        return entities.stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }
}
