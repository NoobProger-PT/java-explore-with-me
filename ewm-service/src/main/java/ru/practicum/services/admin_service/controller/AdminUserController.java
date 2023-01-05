package ru.practicum.services.admin_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.services.admin_service.service.AdminUserService;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Slf4j
@Validated
public class AdminUserController {

    private final AdminUserService service;

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(defaultValue = "-1") List<Long> ids,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get Users with Ids: {}", ids);
        return service.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    public UserDto addUser(@RequestBody @Valid NewUserDto newUserDto) {
        log.info("Add new User with name: {} and Email: {}", newUserDto.getName(), newUserDto.getEmail());
        return service.addUser(newUserDto);
    }

    @DeleteMapping("/users/{userId}")
    public String delete(@PathVariable @Positive long userId) {
        log.info("Delete User with Id: {}", userId);
        return service.delete(userId);
    }
}
