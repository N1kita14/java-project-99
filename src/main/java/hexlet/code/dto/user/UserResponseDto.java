package hexlet.code.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate createdAt;
}
