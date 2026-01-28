package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.IntegrationTest;
import hexlet.code.TestModelGenerator;
import hexlet.code.component.DataInitializer;
import hexlet.code.dto.task.TaskCreateDto;
import hexlet.code.dto.task.TaskUpdateDto;
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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@IntegrationTest
class TaskControllerTest {

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
    private TaskRepository taskRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LabelRepository labelRepository;

    private User testUser;
    private Task testTask;
    private String testUserToken;
    private Label featureLabel;
    private TaskStatus draftStatus;

    @BeforeEach
    void setUp() {
        dataInitializer.initializeRoles();
        dataInitializer.initializeTaskStatuses();
        dataInitializer.initializeLabels();

        featureLabel = labelRepository.findByName("feature").orElseGet(Assertions::fail);
        draftStatus = taskStatusRepository.findBySlug("draft").orElseGet(Assertions::fail);

        testUser = Instancio.of(testModelGenerator.getUserModel()).create();
        testUserToken = jwtUtils.generateToken(testUser.getEmail(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        userRepository.save(testUser);
        testTask = Instancio.of(testModelGenerator.getTaskModel()).create();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(draftStatus);
        testTask.setLabels(Set.of(featureLabel));
        taskRepository.save(testTask);
    }

    @Test
    void getTaskById() throws Exception {
        var request = get("/api/tasks/" + testTask.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        var result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(v -> v.node("id").isEqualTo(testTask.getId()),
                        v -> v.node("index").isEqualTo(testTask.getIndex()),
                        v -> v.node("title").isEqualTo(testTask.getName()),
                        v -> v.node("content").isEqualTo(testTask.getDescription()),
                        v -> v.node("assignee_id").isEqualTo(testUser.getId()),
                        v -> v.node("status").isEqualTo(draftStatus.getSlug()),
                        v -> v.node("taskLabelIds").isArray().containsExactlyInAnyOrder(featureLabel.getId()),
                        v -> v.node("createdAt").isNotNull());
    }

    @Test
    void getTaskByIdNotFound() throws Exception {
        long notExistedId = 99999L;
        var request = get("/api/tasks/" + notExistedId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")));
    }

    @Test
    void getTaskStatuses() throws Exception {
        var request = get("/api/tasks")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        var result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .isArray()
                .isNotEmpty();
    }

    @Test
    void getTaskStatusesWithFiltration() throws Exception {
        var request = get("/api/tasks")
                .param("titleCont", testTask.getName())
                .param("assigneeId", testUser.getId().toString())
                .param("status", draftStatus.getSlug())
                .param("labelId", featureLabel.getId().toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        var result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .isArray()
                .isNotEmpty();

        var request2 = get("/api/tasks")
                .param("status", "noExistedStatus")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        var result2 = mockMvc.perform(request2)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var body2 = result2.getResponse().getContentAsString();
        assertThatJson(body2)
                .isArray()
                .isEmpty();
    }

    @Test
    void createTaskStatus() throws Exception {
        TaskStatus taskStatus = taskStatusRepository.findBySlug("published").orElseGet(Assertions::fail);
        var requestDto = new TaskCreateDto();
        requestDto.setIndex(15123);
        requestDto.setTitle("taskTitle");
        requestDto.setContent("taskContent");
        requestDto.setAssigneeId(testUser.getId());
        requestDto.setStatus(taskStatus.getSlug());
        requestDto.setTaskLabelIds(Set.of(featureLabel.getId()));
        String stringRequestBody = objectMapper.writeValueAsString(requestDto);

        var request = post("/api/tasks")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(stringRequestBody);
        var result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(v -> v.node("index").isEqualTo(requestDto.getIndex()),
                        v -> v.node("title").isEqualTo(requestDto.getTitle()),
                        v -> v.node("content").isEqualTo(requestDto.getContent()),
                        v -> v.node("assignee_id").isEqualTo(testUser.getId()),
                        v -> v.node("status").isEqualTo(taskStatus.getSlug()),
                        v -> v.node("taskLabelIds").isArray().containsExactlyInAnyOrder(featureLabel.getId()),
                        v -> v.node("createdAt").isNotNull());
    }

    @Test
    void updateTaskStatus() throws Exception {
        String taskStatusSlug = "to_review";
        String bugLabel = "bug";
        TaskStatus newTaskStatus = taskStatusRepository.findBySlug(taskStatusSlug).orElseGet(Assertions::fail);
        Label newLabel = labelRepository.findByName(bugLabel).orElseGet(Assertions::fail);
        String newTitle = "newName";
        String newContent = "newSlug";
        Integer newIndex = 1934;
        var requestDto = new TaskUpdateDto();
        requestDto.setTitle(JsonNullable.of(newTitle));
        requestDto.setContent(JsonNullable.of(newContent));
        requestDto.setIndex(JsonNullable.of(newIndex));
        requestDto.setStatus(JsonNullable.of(newTaskStatus.getSlug()));
        requestDto.setTaskLabelIds(JsonNullable.of(Set.of(newLabel.getId())));
        String stringRequestBody = objectMapper.writeValueAsString(requestDto);

        var request = put("/api/tasks/" + testTask.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(stringRequestBody);
        var result = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(v -> v.node("index").isEqualTo(requestDto.getIndex()),
                        v -> v.node("title").isEqualTo(requestDto.getTitle()),
                        v -> v.node("content").isEqualTo(requestDto.getContent()),
                        v -> v.node("assignee_id").isEqualTo(testUser.getId()),
                        v -> v.node("status").isEqualTo(newTaskStatus.getSlug()),
                        v -> v.node("taskLabelIds").isArray().containsExactlyInAnyOrder(newLabel.getId()));
    }

    @Test
    void deleteTaskStatus() throws Exception {
        var request = delete("/api/tasks/" + testTask.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserToken);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        assertThat(taskRepository.findById(testTask.getId())).isEmpty();
    }
}
