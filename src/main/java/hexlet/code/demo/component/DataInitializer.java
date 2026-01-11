package hexlet.code.demo.component;

import hexlet.code.demo.exception.NotFoundException;
import hexlet.code.demo.model.Label;
import hexlet.code.demo.model.Role;
import hexlet.code.demo.model.TaskStatus;
import hexlet.code.demo.model.User;
import hexlet.code.demo.repository.LabelRepository;
import hexlet.code.demo.repository.RoleRepository;
import hexlet.code.demo.repository.TaskStatusRepository;
import hexlet.code.demo.service.impl.CustomUserDetailsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CustomUserDetailsService customUserDetailsService;
    private final RoleRepository roleRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Start initialize default entities");
        initializeRoles();
        initializeUser();
        initializeTaskStatuses();
        initializeLabels();
        log.info("End initialize default entities");
    }

    @Transactional
    public void initializeLabels() {
        Set<String> defaultLabels = Set.of("feature", "bug");
        defaultLabels.forEach(this::createLabelIfNotExist);
    }

    @Transactional
    public void initializeTaskStatuses() {
        Map<String, String> defaultTaskStatuses = Map.of(
                "Draft", "draft",
                "ToReview", "to_review",
                "ToBeFixed", "to_be_fixed",
                "ToPublish", "to_publish",
                "Published", "published");
        defaultTaskStatuses.forEach(this::createTaskStatusIfNotExist);
    }

    @Transactional
    public void initializeRoles() {
        createRoleIfNotExists(Role.ADMIN);
        createRoleIfNotExists(Role.USER);
    }

    @Transactional
    public void initializeUser() {
        String email = "hexlet@example.com";
        String password = "qwerty";
        if (!customUserDetailsService.userExists(email)) {
            var adminRole = roleRepository.findByAuthority(Role.ADMIN)
                    .orElseThrow(() -> new NotFoundException("Role " + Role.ADMIN + " not found!"));
            var user = new User();
            user.setEmail(email);
            user.setPasswordDigest(password);
            user.setRoles(Set.of(adminRole));
            customUserDetailsService.createUser(user);
        }
    }

    private void createRoleIfNotExists(String authority) {
        if (!roleRepository.existsByAuthority(authority)) {
            var role = new Role();
            role.setAuthority(authority);
            roleRepository.save(role);
        }
    }

    private void createTaskStatusIfNotExist(String name, String slug) {
        if (!taskStatusRepository.existsByNameIgnoreCaseOrSlugIgnoreCase(name, slug)) {
            TaskStatus taskStatus = new TaskStatus();
            taskStatus.setName(name);
            taskStatus.setSlug(slug);
            taskStatusRepository.save(taskStatus);
        }
    }

    private void createLabelIfNotExist(String name) {
        if (!labelRepository.existsByName(name)) {
            var label = new Label();
            label.setName(name);
            labelRepository.save(label);
        }
    }
}
