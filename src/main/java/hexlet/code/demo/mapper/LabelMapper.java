package hexlet.code.demo.mapper;

import hexlet.code.demo.dto.label.LabelCreateDto;
import hexlet.code.demo.dto.label.LabelResponseDto;
import hexlet.code.demo.dto.label.LabelUpdateDto;
import hexlet.code.demo.model.Label;
import hexlet.code.demo.repository.LabelRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {JsonNullableMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class LabelMapper {

    @Autowired
    private LabelRepository labelRepository;

    public abstract LabelResponseDto toResponseDto(Label label);

    public abstract Label toEntity(LabelCreateDto dto);

    public abstract void update(LabelUpdateDto updateDto, @MappingTarget Label label);

    @Named("getLabelIds")
    public Set<Long> getLabelIds(Set<Label> labels) {
        if (labels == null) {
            return Collections.emptySet();
        }
        return labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }

    @Named("getLabelsByIds")
    public Set<Label> getLabelsByIds(Set<Long> ids) {
        if (ids == null) {
            return Collections.emptySet();
        }
        List<Label> labels = labelRepository.findAllById(ids);
        return new HashSet<>(labels);
    }
}