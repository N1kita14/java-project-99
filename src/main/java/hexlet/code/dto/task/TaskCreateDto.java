package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TaskCreateDto {

    private Integer index;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @NotBlank
    private String title;

    private String content;

    @NotBlank
    private String status;

    private Set<Long> taskLabelIds;
}
