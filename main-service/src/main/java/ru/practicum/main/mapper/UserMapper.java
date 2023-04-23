package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main.dto.NewUserRequestDto;
import ru.practicum.main.dto.UserDto;
import ru.practicum.main.dto.UserShortDto;
import ru.practicum.main.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);

    User newUserRequestDtoToUser(NewUserRequestDto userDto);

    UserShortDto userToUserShortDto(User user);

    User userShortDtoToUser(UserShortDto userShortDto);
}
