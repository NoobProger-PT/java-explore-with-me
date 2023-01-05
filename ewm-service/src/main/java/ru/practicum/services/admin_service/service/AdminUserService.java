package ru.practicum.services.admin_service.service;

import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface AdminUserService {

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto addUser(NewUserDto newUserDto);

    String delete(long userId);
}
