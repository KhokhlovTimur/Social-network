package ru.itis.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.dto.user.PublicUserDto;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Page with users")
public class UsersPage {
    private Set<? extends PublicUserDto> users;
    private Integer pagesCount;
    private long totalCount;
}
