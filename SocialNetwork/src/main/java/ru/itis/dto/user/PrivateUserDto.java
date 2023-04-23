package ru.itis.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrivateUserDto extends PublicUserDto {
    @Schema(description = "User's email", example = "bob@gmail.com")
    private String email;
    @Schema(description = "User's phone number")
    private String phoneNumber;
    @Schema(description = "User's date of registration")
    private Date dateOfRegistration;
}
