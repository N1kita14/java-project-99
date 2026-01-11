package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.IntegrationTest;
import hexlet.code.TestModelGenerator;
import hexlet.code.component.DataInitializer;
import hexlet.code.dto.label.LabelCreateDto;
import hexlet.code.dto.label.LabelResponseDto;
import hexlet.code.dto.label.LabelUpdateDto;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
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
import java.util.Set;

import static hexlet.code.handler.GlobalExceptionHandler.LABEL_DELETE_ERROR_MESSAGE;
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
class LabelControllerTest {

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
    private LabelRepository labelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelMapper labelMapper;

    private User testUser;
    private String testUserToken;

    @BeforeEach
    void setUp() {
        dataInitializer.initializeRoles();
        dataInitializer.initializeTaskStatuses();
        dataInitializer.initializeLabels();

        testUser = Instancio.of(testModelGenerator.getUserModel()).create();
        testUserToken = jwtUtils.generateToken(testUser.getEmail(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        userRepository.save(testUser);
    }

    @Test
    void getLabelById() throws Exception {
        Label label = labelRepository.findByName("feature").orElseGet(Assertions::fail);

        var request = get("/api/labels/" + label.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        var result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(v -> v.node("id").isEqualTo(label.getId()),
                        v -> v.node("name").isEqualTo(label.getName()),
                        v -> v.node("createdAt").isNotNull());
    }

    @Test
    void getLabelByNotFound() throws Exception {
        long notExistedId = 99999L;
        var request = get("/api/labels/" + notExistedId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")));
    }

    @Test
    void getLabels() throws Exception {
        var request = get("/api/labels")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        var result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        List<LabelResponseDto> responseDtoList = objectMapper.readValue(body, new TypeReference<>() {
        });
        List<LabelResponseDto> expectedDtoList = labelRepository.findAll().stream()
                .map(labelMapper::toResponseDto)
                .toList();
        assertThat(responseDtoList).containsExactlyInAnyOrderElementsOf(expectedDtoList);
    }

    @Test
    void createLabel() throws Exception {
        var requestDto = new LabelCreateDto();
        requestDto.setName("newLabel");
        String stringRequestBody = objectMapper.writeValueAsString(requestDto);

        var request = post("/api/labels")
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
                        v -> v.node("createdAt").isNotNull());
        Label newLabel = labelRepository.findByName(requestDto.getName()).orElse(null);
        assertNotNull(newLabel);
    }

    @Test
    void createLabelWithExistedNameConflict() throws Exception {
        var requestDto = new LabelCreateDto();
        requestDto.setName("bug");
        String stringRequestBody = objectMapper.writeValueAsString(requestDto);

        var request = post("/api/labels")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(stringRequestBody);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString(
                        "Label with name bug already in use")));
    }

    @Test
    void updateLabel() throws Exception {
        String labelName = "bug";
        Label label = labelRepository.findByName(labelName).orElseGet(Assertions::fail);
        String newName = "newLabelName";
        var requestDto = new LabelUpdateDto();
        requestDto.setName(JsonNullable.of(newName));

        String stringRequestBody = objectMapper.writeValueAsString(requestDto);
        var request = put("/api/labels/" + label.getId())
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
                        v -> v.node("name").isEqualTo(newName),
                        v -> v.node("createdAt").isNotNull());
    }

    @Test
    void deleteLabel() throws Exception {
        String labelName = "bug";
        Label label = labelRepository.findByName(labelName).orElseGet(Assertions::fail);

        var request = delete("/api/labels/" + label.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();
        assertThat(labelRepository.findByName(labelName)).isEmpty();
    }

    @Test
    void deleteLabelWithAssignedTaskConflict() throws Exception {
        String labelName = "bug";
        Label label = labelRepository.findByName(labelName).orElseGet(Assertions::fail);

        TaskStatus taskStatus = taskStatusRepository.findBySlug("draft").orElseGet(Assertions::fail);

        Task testTask = Instancio.of(testModelGenerator.getTaskModel()).create();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(taskStatus);
        testTask.setLabels(Set.of(label));
        taskRepository.save(testTask);

        var request = delete("/api/labels/" + label.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString(LABEL_DELETE_ERROR_MESSAGE)));
    }
}