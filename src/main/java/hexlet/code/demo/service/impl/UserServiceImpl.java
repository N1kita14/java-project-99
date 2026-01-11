package hexlet.code.demo.service.impl;

import hexlet.code.demo.dto.user.UserCreateDto;
import hexlet.code.demo.dto.user.UserResponseDto;
import hexlet.code.demo.dto.user.UserUpdateDto;
import hexlet.code.demo.exception.AlreadyExistException;
import hexlet.code.demo.exception.NotFoundException;
import hexlet.code.demo.mapper.UserMapper;
import hexlet.code.demo.model.User;
import hexlet.code.demo.repository.UserRepository;
import hexlet.code.demo.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found!"));
        return userMapper.toResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserResponseDto createUser(UserCreateDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistException("Email " + dto.getEmail() + " already in use!");
        }
        User user = userMapper.toEntity(dto);
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserResponseDto updateUser(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found!"));
        userMapper.update(dto, user);
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}