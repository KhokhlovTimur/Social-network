package ru.itis.dto.posts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.dto.user.PublicUserDto;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PostDto {
    private Long id;
    private String text;
    private Date dateOfPublication;
    private PublicUserDto author;
}
