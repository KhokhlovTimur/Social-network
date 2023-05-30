package ru.itis.dto.posts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.dto.group.GroupDto;
import ru.itis.dto.user.PublicUserDto;
import ru.itis.models.FileInfo;
import ru.itis.models.Post;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PostDto {
    private Long id;
    private String text;
    private Date dateOfPublication;
    private PublicUserDto author;
    private GroupDto group;
    private List<FileInfo> files;
    private Long likesCount;
    private Boolean isLikedByUser;
}
