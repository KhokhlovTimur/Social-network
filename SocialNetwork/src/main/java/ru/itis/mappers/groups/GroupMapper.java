package ru.itis.mappers.groups;

import org.mapstruct.Mapper;
import ru.itis.dto.group.GroupDto;
import ru.itis.models.Group;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    GroupDto toDto(Group group);

    Group toGroup(GroupDto groupDto);
}
