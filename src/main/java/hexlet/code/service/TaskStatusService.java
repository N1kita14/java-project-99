package hexlet.code.service;

import hexlet.code.dto.task_status.TaskStatusCreateDto;
import hexlet.code.dto.task_status.TaskStatusResponseDto;
import hexlet.code.dto.task_status.TaskStatusUpdateDto;

import java.util.List;

public interface TaskStatusService {

    TaskStatusResponseDto getTaskStatusById(Long id);

    List<TaskStatusResponseDto> getAllTaskStatuses();

    TaskStatusResponseDto createTaskStatus(TaskStatusCreateDto dto);

    TaskStatusResponseDto updateTaskStatus(Long id, TaskStatusUpdateDto dto);

    void deleteTaskStatus(Long id);
}
