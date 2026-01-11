package hexlet.code.mapper;

import hexlet.code.dto.task_status.TaskStatusCreateDto;
import hexlet.code.dto.task_status.TaskStatusResponseDto;
import hexlet.code.dto.task_status.TaskStatusUpdateDto;
import hexlet.code.exception.NotFoundException;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {JsonNullableMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TaskStatusMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    public abstract TaskStatusResponseDto toResponseDto(TaskStatus taskStatus);

    public abstract TaskStatus toEntity(TaskStatusCreateDto dto);

    public abstract void update(TaskStatusUpdateDto updateDto, @MappingTarget TaskStatus taskStatus);

    @Named("getTaskStatusBySlag")
    public TaskStatus getTaskStatusBySlag(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("TaskStatus with slug " + slug + " not found!"));
    }
}