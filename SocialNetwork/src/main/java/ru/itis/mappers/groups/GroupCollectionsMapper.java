package ru.itis.mappers.groups;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.itis.dto.group.GroupDto;
import ru.itis.models.Group;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {GroupMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface GroupCollectionsMapper {
    Set<GroupDto> toGroupDtoSet(Set<Group> groups);
    Set<GroupDto> toGroupDtoSet(List<Group> groups);
}
