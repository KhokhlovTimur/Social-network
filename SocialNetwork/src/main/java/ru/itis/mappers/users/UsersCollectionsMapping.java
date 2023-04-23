package ru.itis.mappers.users;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import ru.itis.dto.user.PublicUserDto;
import ru.itis.models.User;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {UsersMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UsersCollectionsMapping {

    @IterableMapping(qualifiedByName = "toPublic")
    Set<PublicUserDto> toGroupDtoSet(Set<User> users);
}
