package ru.itis.mappers.users;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import ru.itis.dto.user.PublicUserDto;
import ru.itis.models.User;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {UsersMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UsersCollectionsMapper {

    @IterableMapping(qualifiedByName = "toPublic")
    Set<PublicUserDto> toPublicUsersDtoSet(Set<User> users);

    Set<PublicUserDto> toPublicUsersDtoSet(List<User> users);

    @IterableMapping(qualifiedByName = "toPublic")
    List<PublicUserDto> toPublicUsersDtoList(List<User> users);
}
