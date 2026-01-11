package hexlet.code.demo.service;

import hexlet.code.demo.dto.task.TaskCreateDto;
import hexlet.code.demo.dto.task.TaskFiltrationDto;
import hexlet.code.demo.dto.task.TaskResponseDto;
import hexlet.code.demo.dto.task.TaskUpdateDto;

import java.util.List;

public interface TaskService {

    TaskResponseDto getTaskById(Long id);

    List<TaskResponseDto> getAllTasks(TaskFiltrationDto filtration);

    TaskResponseDto createTask(TaskCreateDto dto);

    TaskResponseDto updateTask(Long id, TaskUpdateDto dto);

    void deleteTask(Long id);
}
