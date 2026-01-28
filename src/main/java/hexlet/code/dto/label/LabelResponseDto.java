package hexlet.code.dto.label;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
public class LabelResponseDto {
    private Long id;
    private String name;
    private LocalDate createdAt;
}
