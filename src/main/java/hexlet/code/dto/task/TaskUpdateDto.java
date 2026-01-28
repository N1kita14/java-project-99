package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class TaskUpdateDto {

    private JsonNullable<Integer> index;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    @NotBlank
    private JsonNullable<String> title;

    private JsonNullable<String> content;

    @NotBlank
    private JsonNullable<String> status;

    private JsonNullable<Set<Long>> taskLabelIds;

}
