package hexlet.code.dto.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorMessageResponse {
    private String error;
}
