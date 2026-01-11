package hexlet.code.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.demo.IntegrationTest;
import hexlet.code.demo.TestModelGenerator;
import hexlet.code.demo.component.DataInitializer;
import hexlet.code.demo.dto.user.UserCreateDto;
import hexlet.code.demo.dto.user.UserUpdateDto;
import hexlet.code.demo.model.Task;
import hexlet.code.demo.model.TaskStatus;
import hexlet.code.demo.model.User;
import hexlet.code.demo.repository.TaskRepository;
import hexlet.code.demo.repository.TaskStatusRepository;
import hexlet.code.demo.repository.UserRepository;
import hexlet.code.demo.util.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static hexlet.code.demo.handler.GlobalExceptionHandler.USER_DELETE_ERROR_MESSAGE;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @BeforeEach
    void setUp() {
        dataInitializer.initializeRoles();
        dataInitializer.initializeTaskStatuses();
        testUser = Instancio.of(testModelGenerator.getUserModel()).create();
        testUserToken = jwtUtils.generateToken(testUser.getEmail(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        userRepository.save(testUser);
    }

    @Test
    void getUserById() throws Exception {
        var request = get("/api/users/" + testUser.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        var result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(v -> v.node("id").isEqualTo(testUser.getId()),
                        v -> v.node("email").isEqualTo(testUser.getEmail()),
                        v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                        v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                        v -> v.node("createdAt").isNotNull());
    }

    @Test
    void getUserByIdNotFound() throws Exception {
        long notExistedId = 99999L;
        var request = get("/api/users/" + notExistedId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")));
    }

    @Test
    void getUsers() throws Exception {
        var request = get("/api/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        var result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .inPath("[0]")  // check first element
                .and(v -> v.node("id").isEqualTo(testUser.getId()),
                        v -> v.node("email").isEqualTo(testUser.getEmail()),
                        v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                        v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                        v -> v.node("createdAt").isNotNull());
    }

    @Test
    void createUser() throws Exception {
        var requestDto = new UserCreateDto();
        requestDto.setEmail("testemail@test.com");
        requestDto.setPassword("pass123");
        requestDto.setFirstName("Fname");
        requestDto.setLastName("Lname");
        String stringRequestBody = objectMapper.writeValueAsString(requestDto);
        var request = post("/api/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(stringRequestBody);
        var result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(v -> v.node("id").isNotNull(),
                        v -> v.node("email").isEqualTo(requestDto.getEmail()),
                        v -> v.node("firstName").isEqualTo(requestDto.getFirstName()),
                        v -> v.node("lastName").isEqualTo(requestDto.getLastName()),
                        v -> v.node("createdAt").isNotNull());
        User newUser = userRepository.findByEmail(requestDto.getEmail()).orElse(null);
        assertNotNull(newUser);
        assertNotNull(newUser.getPasswordDigest());
    }

    @Test
    void createUserValidationError() throws Exception {
        var requestDto = new UserCreateDto();
        requestDto.setEmail("testemail@test.com");
        requestDto.setFirstName("Fname");
        requestDto.setLastName("Lname");
        String stringRequestBody = objectMapper.writeValueAsString(requestDto);
        var request = post("/api/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(stringRequestBody);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "Ошибка валидации в полях")));
    }

    @Test
    void updateUser() throws Exception {
        String newEmail = "newmail@test.com";
        String newFirstName = "newFname";
        var requestDto = new UserUpdateDto();
        requestDto.setEmail(JsonNullable.of(newEmail));
        requestDto.setFirstName(JsonNullable.of(newFirstName));
        requestDto.setPassword(JsonNullable.of("newpass123"));

        String stringRequestBody = objectMapper.writeValueAsString(requestDto);
        var request = put("/api/users/" + testUser.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(stringRequestBody);
        var result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(v -> v.node("id").isNotNull(),
                        v -> v.node("email").isEqualTo(newEmail),
                        v -> v.node("firstName").isEqualTo(newFirstName),
                        v -> v.node("lastName").isEqualTo(testUser.getLastName()));
    }

    @Test
    void deleteUser() throws Exception {
        var request = delete("/api/users/" + testUser.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();
        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    void deleteUserWithAssignedTaskConflict() throws Exception {
        TaskStatus draftStatus = taskStatusRepository.findBySlug("draft").orElseGet(Assertions::fail);
        Task testTask = Instancio.of(testModelGenerator.getTaskModel()).create();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(draftStatus);
        taskRepository.save(testTask);

        var request = delete("/api/users/" + testUser.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString(USER_DELETE_ERROR_MESSAGE)));
    }
}