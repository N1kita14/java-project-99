package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDto;
import hexlet.code.dto.task.TaskFiltrationDto;
import hexlet.code.dto.task.TaskResponseDto;
import hexlet.code.dto.task.TaskUpdateDto;

import java.util.List;

public interface TaskService {

    TaskResponseDto getTaskById(Long id);

    List<TaskResponseDto> getAllTasks(TaskFiltrationDto filtration);

    TaskResponseDto createTask(TaskCreateDto dto);

    TaskResponseDto updateTask(Long id, TaskUpdateDto dto);

    void deleteTask(Long id);
}
