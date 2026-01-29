package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.IntegrationTest;
import hexlet.code.TestModelGenerator;
import hexlet.code.component.DataInitializer;
import hexlet.code.dto.task_status.TaskStatusCreateDto;
import hexlet.code.dto.task_status.TaskStatusResponseDto;
import hexlet.code.dto.task_status.TaskStatusUpdateDto;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.JWTUtils;
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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@IntegrationTest
public class TaskStatusControllerTest {

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
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    private User testUser;
    private String testUserToken;
    private TaskStatus dataftTaskStatus;

    @BeforeEach
    void setUp() {
        dataInitializer.initializeRoles();
        dataInitializer.initializeTaskStatuses();
        taskRepository.deleteAll();
        testUser = Instancio.of(testModelGenerator.getUserModel()).create();
        testUserToken = jwtUtils.generateToken(testUser.getEmail(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        userRepository.save(testUser);
        dataftTaskStatus = taskStatusRepository.findBySlug("draft").orElseGet(Assertions::fail);
    }

    @Test
    void getTaskStatusById() throws Exception {
        var request = get("/api/task_statuses/" + dataftTaskStatus.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        var result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(v -> v.node("id").isEqualTo(dataftTaskStatus.getId()),
                        v -> v.node("name").isEqualTo(dataftTaskStatus.getName()),
                        v -> v.node("slug").isEqualTo(dataftTaskStatus.getSlug()),
                        v -> v.node("createdAt").isNotNull());
    }

    @Test
    void getTaskStatusByNotFound() throws Exception {
        long notExistedId = 99999L;
        var request = get("/api/task_statuses/" + notExistedId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")));
    }

    @Test
    void getTaskStatuses() throws Exception {
        var request = get("/api/task_statuses")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        var result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        List<TaskStatusResponseDto> responseDtoList = objectMapper.readValue(body, new TypeReference<>() {
        });
        List<TaskStatusResponseDto> expectedDtoList = taskStatusRepository.findAll().stream()
                .map(taskStatusMapper::toResponseDto)
                .toList();
        assertThat(responseDtoList).containsExactlyInAnyOrderElementsOf(expectedDtoList);
    }

    @Test
    void createTaskStatus() throws Exception {
        var requestDto = new TaskStatusCreateDto();
        requestDto.setName("taskStatusName");
        requestDto.setSlug("taskStatusSlug");
        String stringRequestBody = objectMapper.writeValueAsString(requestDto);
        var request = post("/api/task_statuses")
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
                        v -> v.node("name").isEqualTo(requestDto.getName()),
                        v -> v.node("slug").isEqualTo(requestDto.getSlug()),
                        v -> v.node("createdAt").isNotNull());
        TaskStatus newTaskStatus = taskStatusRepository.findBySlug(requestDto.getSlug()).orElse(null);
        assertNotNull(newTaskStatus);
    }

    @Test
    void createTaskStatusExistedError() throws Exception {
        var requestDto = new TaskStatusCreateDto();
        requestDto.setName(dataftTaskStatus.getName());
        requestDto.setSlug(dataftTaskStatus.getSlug());
        String stringRequestBody = objectMapper.writeValueAsString(requestDto);

        var request = post("/api/task_statuses")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(stringRequestBody);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void updateTaskStatus() throws Exception {
        String newName = "newName";
        String newSlug = "newSlug";
        var requestDto = new TaskStatusUpdateDto();
        requestDto.setName(JsonNullable.of(newName));
        requestDto.setSlug(JsonNullable.of(newSlug));

        String stringRequestBody = objectMapper.writeValueAsString(requestDto);
        var request = put("/api/task_statuses/" + dataftTaskStatus.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(stringRequestBody);

        var result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(v -> v.node("id").isEqualTo(dataftTaskStatus.getId()),
                        v -> v.node("name").isEqualTo(newName),
                        v -> v.node("slug").isEqualTo(newSlug),
                        v -> v.node("createdAt").isNotNull());
    }

    @Test
    void deleteTaskStatus() throws Exception {
        String statusName = dataftTaskStatus.getName();
        var request = delete("/api/task_statuses/" + dataftTaskStatus.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();
        assertThat(taskStatusRepository.findBySlug(dataftTaskStatus.getSlug())).isEmpty();
    }

    @Test
    void deleteTaskStatusWithAssignedTaskConflict() throws Exception {
        Task testTask = Instancio.of(testModelGenerator.getTaskModel()).create();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(dataftTaskStatus);
        taskRepository.saveAndFlush(testTask);

        assertThat(taskRepository.existsByTaskStatusId(dataftTaskStatus.getId())).isTrue();
        var request = delete("/api/task_statuses/" + dataftTaskStatus.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isConflict());

        assertThat(taskStatusRepository.findBySlug(dataftTaskStatus.getSlug())).isNotEmpty();
    }

}
