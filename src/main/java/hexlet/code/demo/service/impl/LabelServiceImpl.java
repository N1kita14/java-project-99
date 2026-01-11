package hexlet.code.service.impl;


import hexlet.code.dto.label.LabelCreateDto;
import hexlet.code.dto.label.LabelResponseDto;
import hexlet.code.dto.label.LabelUpdateDto;
import hexlet.code.exception.AlreadyExistException;
import hexlet.code.exception.NotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public LabelResponseDto getLabelById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Label with id " + id + " not found!"));
        return labelMapper.toResponseDto(label);
    }

    @Override
    public List<LabelResponseDto> getAllLables() {
        return labelRepository.findAll().stream()
                .map(labelMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public LabelResponseDto createLabel(LabelCreateDto dto) {
        if (labelRepository.existsByName(dto.getName())) {
            throw new AlreadyExistException("Label with name " + dto.getName() + " already in use!");
        }
        Label label = labelMapper.toEntity(dto);
        return labelMapper.toResponseDto(labelRepository.save(label));
    }

    @Transactional
    @Override
    public LabelResponseDto updateLabel(Long id, LabelUpdateDto dto) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Label with id " + id + " not found!"));
        labelMapper.update(dto, label);
        return labelMapper.toResponseDto(labelRepository.save(label));
    }

    @Transactional
    @Override
    public void deleteLabel(Long id) {
        labelRepository.deleteById(id);
    }
}