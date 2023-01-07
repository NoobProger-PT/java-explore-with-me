package ru.practicum.services.admin_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.user_exception.UserNotFound;
import ru.practicum.services.admin_service.repository.AdminUsersRepository;
import ru.practicum.services.admin_service.service.AdminUserService;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUsersRepository usersRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        List<UserDto> result;
        if (ids.isEmpty()) {
            result = usersRepository.findAll(PageRequest.of(from, size, Sort.by("id").ascending())).stream()
                    .map(UserMapper::mapToUserDto).collect(Collectors.toList());
        } else {
            result = usersRepository.findAllByIdIn(ids).stream()
                    .map(UserMapper::mapToUserDto).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserDto newUserDto) {
        User user = usersRepository.save(UserMapper.mapToUserFromNewUserDto(newUserDto));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public String delete(long userId) {
        usersRepository.findById(userId).orElseThrow(() ->
                new UserNotFound("Пользователь с id: " + userId + " не найден"));
        usersRepository.deleteById(userId);
        return "Пользователь с Id: " + userId + " удален";
    }
}
