package hexlet.code.demo.controller;

import hexlet.code.demo.dto.task.TaskCreateDto;
import hexlet.code.demo.dto.task.TaskFiltrationDto;
import hexlet.code.demo.dto.task.TaskResponseDto;
import hexlet.code.demo.dto.task.TaskUpdateDto;
import hexlet.code.demo.repository.TaskRepository;
import hexlet.code.demo.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
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
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskRepository taskRepository;

    @GetMapping("/{id}")
    public TaskResponseDto getTask(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(@ParameterObject TaskFiltrationDto filtration) {
        List<TaskResponseDto> responseDtoList = taskService.getAllTasks(filtration);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(responseDtoList.size()))
                .body(responseDtoList);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDto createTask(@Valid @RequestBody TaskCreateDto dto) {
        return taskService.createTask(dto);
    }

    @PutMapping("/{id}")
    public TaskResponseDto updateTask(@PathVariable Long id,
                                      @Valid @RequestBody TaskUpdateDto dto) {
        return taskService.updateTask(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsByTaskStatusId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
