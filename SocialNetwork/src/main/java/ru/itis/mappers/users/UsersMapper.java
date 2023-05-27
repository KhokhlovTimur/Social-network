package ru.itis.mappers.users;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.itis.dto.user.*;
import ru.itis.models.FriendRequest;
import ru.itis.models.User;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    User toUser(PrivateUserDto userDto);

    UserUpdateResponseDto toUpdateResponseDto(User user);

    User toUser(PublicUserDto userDto);

    User toUser(UserUpdateDto userDto);

    PrivateUserDto toDto(UserSignUpDto signUpDto);

    @Named("toPublic")
    PublicUserDto toPublicDto(User user);

    @Named("toPrivate")
    PrivateUserDto toPrivateDto(User user);

    @Named("toFriendResponse")
    UserFriendResponseDto toUserFriendResponseDto(User user);

    FriendResponseDto toFriendResponseDto(FriendRequest friendRequest);
}
