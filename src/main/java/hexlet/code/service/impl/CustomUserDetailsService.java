package hexlet.code.service.impl;

import hexlet.code.exception.NotFoundException;
import hexlet.code.model.Role;
import hexlet.code.model.User;
import hexlet.code.repository.RoleRepository;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsManager {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public void createUser(UserDetails userDetails) {
        User user = new User();
        user.setEmail(userDetails.getUsername());
        String hashedPassword = passwordEncoder.encode(userDetails.getPassword());
        user.setPasswordDigest(hashedPassword);

        if (userDetails.getAuthorities() != null && !userDetails.getAuthorities().isEmpty()) {
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            Set<Role> roles = new HashSet<>();
            for (GrantedAuthority authority : authorities) {
                if (authority instanceof Role role) {
                    roles.add(role);
                } else {
                    Optional<Role> roleOpt = roleRepository.findByAuthority(authority.getAuthority());
                    roleOpt.ifPresent(roles::add);
                }
            }
            if (roles.isEmpty()) {
                Role userRole = roleRepository.findByAuthority(Role.USER)
                        .orElseThrow(() -> new NotFoundException("Role " + Role.USER + " not found!"));
                user.setRoles(Set.of(userRole));
            } else {
                user.setRoles(roles);
            }
        }

        userRepository.save(user);
    }

    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public void deleteUser(String username) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByEmail(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails details = userRepository.findByEmailWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.info("loadUser by userName : {}, {}", details, details.getAuthorities());
        return details;
    }
}
