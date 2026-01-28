package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDto;
import hexlet.code.dto.label.LabelResponseDto;
import hexlet.code.dto.label.LabelUpdateDto;

import java.util.List;

public interface LabelService {

    LabelResponseDto getLabelById(Long id);

    List<LabelResponseDto> getAllLables();

    LabelResponseDto createLabel(LabelCreateDto dto);

    LabelResponseDto updateLabel(Long id, LabelUpdateDto dto);

    void deleteLabel(Long id);
}
