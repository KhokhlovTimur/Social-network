package ru.itis.mappers.posts;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.itis.dto.posts.PostDto;
import ru.itis.models.Post;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = PostsMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PostsCollectionMapper {
    List<PostDto> toDtoList(List<Post> posts);
}
