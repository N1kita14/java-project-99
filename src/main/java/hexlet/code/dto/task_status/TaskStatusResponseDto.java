package hexlet.code.dto.task_status;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
public class TaskStatusResponseDto {
    private Long id;
    private String name;
    private String slug;
    private LocalDate createdAt;
}
