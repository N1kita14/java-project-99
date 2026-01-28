package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDto;
import hexlet.code.dto.user.UserResponseDto;
import hexlet.code.dto.user.UserUpdateDto;

import java.util.List;

public interface UserService {

    UserResponseDto getUserById(Long id);

    List<UserResponseDto> getAllUsers();

    UserResponseDto createUser(UserCreateDto dto);

    UserResponseDto updateUser(Long id, UserUpdateDto dto);

    void deleteUser(Long id);
}
