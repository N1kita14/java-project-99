package hexlet.code.demo.controller;

import hexlet.code.demo.dto.label.LabelCreateDto;
import hexlet.code.demo.dto.label.LabelResponseDto;
import hexlet.code.demo.dto.label.LabelUpdateDto;
import hexlet.code.demo.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @GetMapping("/{id}")
    public LabelResponseDto getLabel(@PathVariable Long id) {
        return labelService.getLabelById(id);
    }

    @GetMapping
    public ResponseEntity<List<LabelResponseDto>> getAllLabels() {
        List<LabelResponseDto> responseDtoList = labelService.getAllLables();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(responseDtoList.size()))
                .body(responseDtoList);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseDto createLabel(@Valid @RequestBody LabelCreateDto dto) {
        return labelService.createLabel(dto);
    }

    @PutMapping("/{id}")
    public LabelResponseDto updateLabel(@PathVariable Long id,
                                        @Valid @RequestBody LabelUpdateDto dto) {
        return labelService.updateLabel(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
    }
}
