package hexlet.code.demo.controller;

import hexlet.code.demo.dto.task.TaskCreateDto;
import hexlet.code.demo.dto.task.TaskFiltrationDto;
import hexlet.code.demo.dto.task.TaskResponseDto;
import hexlet.code.demo.dto.task.TaskUpdateDto;
import hexlet.code.demo.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}