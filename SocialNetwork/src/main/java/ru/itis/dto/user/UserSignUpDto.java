package ru.itis.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Model for user's registration or updating")
public class UserSignUpDto {
    @Schema(description = "User's name", example = "Bob", required = true)
    private String name;
    @Schema(description = "User's surname", example = "Henderson", required = true)
    private String surname;
    @Schema(description = "User's username", example = "hend222", required = true)
    private String username;
    @Schema(description = "User's age", example = "23", required = true)
    private Integer age;
    @Schema(description = "User's password", required = true)
    private String password;
    @Schema(description = "User's phone number", required = false)
    private String phoneNumber;
//    @Schema(description = "User's email", example = "bob@gmail.com")
//    private String email;
    @Schema(description = "User's gender", example = "male")
    private String gender;
}
