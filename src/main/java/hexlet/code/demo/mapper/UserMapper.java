package hexlet.code.demo.mapper;

import hexlet.code.demo.dto.user.UserCreateDto;
import hexlet.code.demo.dto.user.UserResponseDto;
import hexlet.code.demo.dto.user.UserUpdateDto;
import hexlet.code.demo.exception.NotFoundException;
import hexlet.code.demo.model.Role;
import hexlet.code.demo.model.User;
import hexlet.code.demo.repository.RoleRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import org.mapstruct.AfterMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {JsonNullableMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    public abstract UserResponseDto toResponseDto(User user);

    @Mapping(target = "passwordDigest", ignore = true)
    public abstract User toEntity(UserCreateDto dto);

    @AfterMapping
    public void afterMapping(@MappingTarget User user, UserCreateDto dto) {
        user.setPasswordDigest(passwordEncoder.encode(dto.getPassword()));

        Role useRole = roleRepository.findByAuthority(Role.USER)
                .orElseThrow(() -> new NotFoundException("Role " + Role.USER + " not found!"));
        user.setRoles(Set.of(useRole));
    }

    @Mapping(target = "passwordDigest", ignore = true)
    public abstract void update(UserUpdateDto updateDto, @MappingTarget User user);

    @AfterMapping
    public void afterMapping(@MappingTarget User user, UserUpdateDto dto) {
        if (dto.getPassword() != null && dto.getPassword().isPresent()) {
            String password = dto.getPassword().get();
            String encryptedPassword = passwordEncoder.encode(password);
            user.setPasswordDigest(encryptedPassword);
        }
    }
}
