package ru.itis.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Model for user's registration or updating")
public class UserSignUpDto {
    @Schema(description = "User's name", example = "Bob", required = true)
    @Size(min = 1)
    private String name;

    @Schema(description = "User's surname", example = "Henderson", required = true)
    @Size(min = 1)
    private String surname;

    @Schema(description = "User's username", example = "hend222", required = true)
    private String username;

    @Schema(description = "User's age", example = "23", required = false)
    @Range(min = 10, max = 150, message = "{age.incorrect}")
    private int age;

    @Schema(description = "User's password", required = true)
    @Size(min = 6, max = 25, message = "{password.incorrect-size}")
    private String password;

    @Schema(description = "User's phone number")
    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}")
    private String phoneNumber;

    @Schema(description = "User's email", example = "bob@gmail.com")
    @Email
    private String email;

    @Schema(description = "User's gender", example = "male")
    private String gender;
}
