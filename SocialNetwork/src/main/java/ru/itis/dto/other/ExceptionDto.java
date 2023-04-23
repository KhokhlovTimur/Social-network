package ru.itis.dto.other;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Error information")
public class ExceptionDto {
    @Schema(description = "Message")
    private String message;
    @Schema(description = "Error's status")
    private int status;
}
