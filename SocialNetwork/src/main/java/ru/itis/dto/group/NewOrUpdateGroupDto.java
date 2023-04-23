package ru.itis.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Add or update group dto")
public class NewOrUpdateGroupDto {
    @Schema(description = "Group's name", example = "Films")
    private String name;
    @Schema(description = "Group's description", example = "Group about films")
    private String description;
}
