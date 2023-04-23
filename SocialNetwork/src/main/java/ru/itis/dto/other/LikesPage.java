package ru.itis.dto.other;

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
public class LikesPage {
    private Set<? extends PublicUserDto> users;
    private Long totalCount;
}
