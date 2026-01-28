package hexlet.code.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDto {

    @Email
    @NotBlank
    private JsonNullable<String> email;

    @NotBlank
    private JsonNullable<String> firstName;

    @NotBlank
    private JsonNullable<String> lastName;

    @NotBlank
    @Size(min = 3)
    private JsonNullable<String> password;
}
