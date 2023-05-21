package ru.itis.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.dto.group.GroupDto;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Page with groups")
public class GroupsPage {
    private Set<GroupDto> groups;
    private long totalCount;
    private Integer pagesCount;
}
