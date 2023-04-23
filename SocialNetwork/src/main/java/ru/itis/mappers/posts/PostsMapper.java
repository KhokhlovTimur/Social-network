package ru.itis.mappers.posts;

import org.mapstruct.Mapper;
import ru.itis.dto.posts.PostDto;
import ru.itis.models.Post;

@Mapper(componentModel = "spring")
public interface PostsMapper {
    PostDto toDto(Post post);

    Post toPost(PostDto postDto);
}
