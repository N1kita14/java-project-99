package hexlet.code.dto.error;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationError {
    public static final String VALIDATION_MESSAGE = "Ошибка валидации в полях";
    private List<Violation> violations = new ArrayList<>();

    public String getValidationErrorMessage() {
        return ValidationError.VALIDATION_MESSAGE + ": " + violations;
    }
}
