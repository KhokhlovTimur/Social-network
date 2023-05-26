package ru.itis.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.itis.dto.other.TokensDto;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserUpdateResponseDto extends PrivateUserDto {
    private TokensDto tokens;
}
