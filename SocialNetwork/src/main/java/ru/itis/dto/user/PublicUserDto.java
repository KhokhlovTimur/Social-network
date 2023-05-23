package ru.itis.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicUserDto {
    private Long id;
    @Schema(description = "User's name", example = "Bob")
    private String name;
    @Schema(description = "User's surname", example = "Kane")
    private String surname;
    @Schema(description = "User's username", example = "bob122")
    private String username;
    @Schema(description = "User's age", example = "22")
    private Integer age;
    @Schema(description = "User's bio", example = "May the force come with you")
    private String bio;
    @Schema(description = "User's avatar's link")
    private String avatarLink;
    @Schema(description = "User's gender", example = "male")
    private String gender;
    @Schema(description = "User's email", example = "bob@gmail.com")
    private String email;
    @Schema(description = "User's phone number")
    private String phoneNumber;
}
