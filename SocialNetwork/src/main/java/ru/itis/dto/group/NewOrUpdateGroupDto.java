package ru.itis.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.validation.constraints.UniqueGroupName;

import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Add or update group dto")
public class NewOrUpdateGroupDto {
    @Schema(description = "Group's name", example = "Films")
    @Size(min = 1, max = 50, message = "{group.incorrect-name}")
    private String name;

    @Schema(description = "Group's description", example = "Group about films")
    @Size(max = 300,  message = "{group.incorrect-description}")
    private String description;

    private MultipartFile image;
}
