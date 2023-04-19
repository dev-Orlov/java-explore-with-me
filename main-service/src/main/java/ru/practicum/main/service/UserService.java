package ru.practicum.main.service;

import ru.practicum.main.dto.NewUserRequestDto;
import ru.practicum.main.dto.UserDto;

import javax.transaction.Transactional;
import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    @Transactional
    UserDto create(NewUserRequestDto userDto);

    @Transactional
    UserDto delete(Long userId);
}
