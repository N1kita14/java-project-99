package hexlet.code.demo.service.impl;

import hexlet.code.demo.dto.label.LabelCreateDto;
import hexlet.code.demo.dto.label.LabelResponseDto;
import hexlet.code.demo.dto.label.LabelUpdateDto;
import hexlet.code.demo.exception.AlreadyExistException;
import hexlet.code.demo.exception.NotFoundException;
import hexlet.code.demo.mapper.LabelMapper;
import hexlet.code.demo.model.Label;
import hexlet.code.demo.repository.LabelRepository;
import hexlet.code.demo.service.LabelService;
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