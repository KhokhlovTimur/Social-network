package ru.itis.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.dto.user.PublicUserDto;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Group")
public class GroupDto {
    @Schema(description = "Group's identifier", example = "1")
    private Long id;
    @Schema(description = "Group's name", example = "Films")
    private String name;
    @Schema(description = "Group's description", example = "Group about films")
    private String description;
    @Schema(description = "Date of creation")
    private Date dateOfCreation;
    @Schema(description = "Group's creator")
    private PublicUserDto creator;
    private String imageLink;
}
