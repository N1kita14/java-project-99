package hexlet.code.demo.service;

import hexlet.code.demo.dto.user.UserCreateDto;
import hexlet.code.demo.dto.user.UserResponseDto;
import hexlet.code.demo.dto.user.UserUpdateDto;

import java.util.List;

public interface UserService {

    UserResponseDto getUserById(Long id);

    List<UserResponseDto> getAllUsers();

    UserResponseDto createUser(UserCreateDto dto);

    UserResponseDto updateUser(Long id, UserUpdateDto dto);

    void deleteUser(Long id);
}