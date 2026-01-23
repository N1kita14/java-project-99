package hexlet.code.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.demo.IntegrationTest;
import hexlet.code.demo.TestModelGenerator;
import hexlet.code.demo.component.DataInitializer;
import hexlet.code.demo.model.User;
import hexlet.code.demo.repository.TaskRepository;
import hexlet.code.demo.repository.TaskStatusRepository;
import hexlet.code.demo.repository.UserRepository;
import hexlet.code.demo.util.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@IntegrationTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestModelGenerator testModelGenerator;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private User testUser;
    private String testUserToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        dataInitializer.initializeRoles();
        dataInitializer.initializeTaskStatuses();
        testUser = Instancio.of(testModelGenerator.getUserModel()).create();
        testUserToken = jwtUtils.generateToken(testUser.getEmail(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        adminToken = jwtUtils.generateToken(testUser.getEmail(),
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        userRepository.save(testUser);
    }

    @Test
    void deleteUserUser() throws Exception {
        var request = delete("/api/users/" + testUser.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())  // Ожидаем статус 204
                .andReturn();

        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }

    @Test
    void deleteUser() throws Exception {
        User anotherUser = Instancio.of(testModelGenerator.getUserModel()).create();
        userRepository.save(anotherUser);

        var request = delete("/api/users/" + anotherUser.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void deleteUserAdmin() throws Exception {
        User anotherUser = Instancio.of(testModelGenerator.getUserModel()).create();
        userRepository.save(anotherUser);

        var request = delete("/api/users/" + anotherUser.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())  // Ожидаем статус 204
                .andReturn();

        assertThat(userRepository.findById(anotherUser.getId())).isEmpty();
    }
}
