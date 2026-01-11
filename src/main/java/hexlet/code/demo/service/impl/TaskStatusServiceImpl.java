package hexlet.code.demo.service.impl;

import hexlet.code.demo.dto.task_status.TaskStatusCreateDto;
import hexlet.code.demo.dto.task_status.TaskStatusResponseDto;
import hexlet.code.demo.dto.task_status.TaskStatusUpdateDto;
import hexlet.code.demo.exception.AlreadyExistException;
import hexlet.code.demo.exception.NotFoundException;
import hexlet.code.demo.mapper.TaskStatusMapper;
import hexlet.code.demo.model.TaskStatus;
import hexlet.code.demo.repository.TaskStatusRepository;
import hexlet.code.demo.service.TaskStatusService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    @Override
    public TaskStatusResponseDto getTaskStatusById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("TaskStatus with id " + id + " not found!"));
        return taskStatusMapper.toResponseDto(taskStatus);
    }

    @Override
    public List<TaskStatusResponseDto> getAllTaskStatuses() {
        return taskStatusRepository.findAll().stream()
                .map(taskStatusMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public TaskStatusResponseDto createTaskStatus(TaskStatusCreateDto dto) {
        if (taskStatusRepository.existsByNameIgnoreCaseOrSlugIgnoreCase(dto.getName(), dto.getSlug())) {
            throw new AlreadyExistException(
                    String.format("TaskStatus.name or TaskStatus.slug (%s or %s) already in use!",
                            dto.getName(),
                            dto.getSlug()));
        }
        TaskStatus taskStatus = taskStatusMapper.toEntity(dto);
        return taskStatusMapper.toResponseDto(taskStatusRepository.save(taskStatus));
    }

    @Transactional
    @Override
    public TaskStatusResponseDto updateTaskStatus(Long id, TaskStatusUpdateDto dto) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("TaskStatus with id " + id + " not found!"));
        taskStatusMapper.update(dto, taskStatus);
        return taskStatusMapper.toResponseDto(taskStatusRepository.save(taskStatus));
    }

    @Transactional
    @Override
    public void deleteTaskStatus(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
