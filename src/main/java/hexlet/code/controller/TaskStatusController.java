package hexlet.code.controller;

import hexlet.code.dto.task_status.TaskStatusCreateDto;
import hexlet.code.dto.task_status.TaskStatusResponseDto;
import hexlet.code.dto.task_status.TaskStatusUpdateDto;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import hexlet.code.repository.TaskRepository;
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
    private final TaskRepository taskRepository;

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
    public ResponseEntity<Void> updateTaskStatus(@PathVariable Long id,
                                                 @Valid @RequestBody TaskStatusUpdateDto dto) {
        taskStatusService.updateTaskStatus(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskStatus(@PathVariable Long id) {
        if (taskRepository.existsByTaskStatusId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        taskStatusService.deleteTaskStatus(id);
        return ResponseEntity.noContent().build();
    }

}
