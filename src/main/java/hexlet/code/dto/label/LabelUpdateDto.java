package hexlet.code.dto.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class LabelUpdateDto {

    @NotBlank
    @Size(min = 3, max = 1000)
    private JsonNullable<String> name;
}
