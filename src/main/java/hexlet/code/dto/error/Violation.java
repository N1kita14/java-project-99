package hexlet.code.dto.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Violation {
    private String fieldName;
    private String message;

    @Override
    public String toString() {
        return "{" + fieldName + ": " + message + "}";
    }
}
