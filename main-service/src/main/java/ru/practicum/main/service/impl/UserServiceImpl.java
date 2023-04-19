package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.NewUserRequestDto;
import ru.practicum.main.dto.UserDto;
import ru.practicum.main.exception.AlreadyExistsException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.UserMapper;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.UserService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable page = PageRequest.of(from, size);
        List<UserDto> userList = userRepository.findAllByIdIn(ids, page).stream().map(userMapper::userToUserDto)
                .collect(Collectors.toList());

        log.debug("Получен список пользователей");
        return userList;
    }

    @Override
    @Transactional
    public UserDto create(NewUserRequestDto userDto) {
        User user = userMapper.newUserRequestDtoToUser(userDto);

        try {
            userRepository.save(user);

            log.debug("Сохранён объект пользователя: {}", user);
            return userMapper.userToUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException("Пользователь с таким email " + userDto.getEmail() +
                    " уже зарегистрирован");
        }
    }

    @Override
    @Transactional
    public UserDto delete(Long userId) {
        UserDto userDto = userMapper.userToUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found.")));
        userRepository.deleteById(userId);

        log.debug("Удалён объект пользователя с id={}", userId);
        return userDto;
    }
}
