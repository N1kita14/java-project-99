package hexlet.code.dto.task_status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskStatusUpdateDto {

    @NotBlank
    @Size(min = 1)
    private JsonNullable<String> name;

    @NotBlank
    @Size(min = 1)
    private JsonNullable<String> slug;
}
