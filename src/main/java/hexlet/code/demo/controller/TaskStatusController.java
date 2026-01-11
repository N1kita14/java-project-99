package hexlet.code.demo.controller;

import hexlet.code.demo.dto.task_status.TaskStatusCreateDto;
import hexlet.code.demo.dto.task_status.TaskStatusResponseDto;
import hexlet.code.demo.dto.task_status.TaskStatusUpdateDto;
import hexlet.code.demo.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/task_statuses")
@RequiredArgsConstructor
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    @GetMapping("/{id}")
    public TaskStatusResponseDto getTaskStatus(@PathVariable Long id) {
        return taskStatusService.getTaskStatusById(id);
    }

    @GetMapping
    public ResponseEntity<List<TaskStatusResponseDto>> getAllTaskStatuses() {
        List<TaskStatusResponseDto> responseDtoList = taskStatusService.getAllTaskStatuses();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(responseDtoList.size()))
                .body(responseDtoList);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusResponseDto createTaskStatus(@Valid @RequestBody TaskStatusCreateDto dto) {
        return taskStatusService.createTaskStatus(dto);
    }

    @PutMapping("/{id}")
    public TaskStatusResponseDto updateTaskStatus(@PathVariable Long id,
                                                  @Valid @RequestBody TaskStatusUpdateDto dto) {
        return taskStatusService.updateTaskStatus(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskStatus(@PathVariable Long id) {
        taskStatusService.deleteTaskStatus(id);
    }
}
