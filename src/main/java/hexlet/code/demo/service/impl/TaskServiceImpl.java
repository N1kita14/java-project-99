package hexlet.code.demo.service.impl;

import hexlet.code.demo.dto.task.TaskCreateDto;
import hexlet.code.demo.dto.task.TaskFiltrationDto;
import hexlet.code.demo.dto.task.TaskResponseDto;
import hexlet.code.demo.dto.task.TaskUpdateDto;
import hexlet.code.demo.exception.NotFoundException;
import hexlet.code.demo.mapper.TaskMapper;
import hexlet.code.demo.model.Task;
import hexlet.code.demo.repository.TaskRepository;
import hexlet.code.demo.repository.specification.TaskSpecification;
import hexlet.code.demo.service.TaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;

    @Override
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task with id " + id + " not found!"));
        return taskMapper.toResponseDto(task);
    }

    @Override
    public List<TaskResponseDto> getAllTasks(TaskFiltrationDto filtration) {
        Specification<Task> spec = taskSpecification.build(filtration);
        return taskRepository.findAll(spec).stream()
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public TaskResponseDto createTask(TaskCreateDto dto) {
        Task task = taskMapper.toEntity(dto);
        return taskMapper.toResponseDto(taskRepository.save(task));
    }

    @Transactional
    @Override
    public TaskResponseDto updateTask(Long id, TaskUpdateDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task with id " + id + " not found!"));
        taskMapper.update(dto, task);
        return taskMapper.toResponseDto(taskRepository.save(task));
    }

    @Transactional
    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}