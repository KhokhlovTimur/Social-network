package ru.itis.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.dto.other.TokensDto;
import ru.itis.validation.constraints.UniqueUsername;

import javax.validation.constraints.Email;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdateDto {
    @Schema(description = "User's name", example = "Bob", required = true)
    @Size(min = 1, max = 50, message = "{name.incorrect-size}")
    private String name;

    @Schema(description = "User's surname", example = "Henderson", required = true)
    @Size(min = 1, max = 50, message = "{surname.incorrect-size}")
    private String surname;

    @Schema(description = "User's username", example = "hend222")
    @Size(min = 1, max = 50, message = "{username.incorrect-size}")
    private String username;

    @Schema(description = "User's age", example = "23")
    @Range(min = 1, max = 150, message = "{age.incorrect}")
    private Integer age;

    @Schema(description = "User's last password")
    private String password;

    @Schema(description = "User's new password")
    private String newPassword;

    @Schema(description = "User's phone number")
//    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}")
    private String phoneNumber;

    @Schema(description = "User's email", example = "bob@gmail.com")
//    @Email(message = "{email.incorrect-format}")
    private String email;

    @Schema(description = "User's avatar", example = "male")
    private MultipartFile avatar;

    @Schema(description = "User's bio", example = "May the force come with you")
    @Size(min = 1, max = 255, message = "{bio.incorrect-size}")
    private String bio;

    private String gender;
}
