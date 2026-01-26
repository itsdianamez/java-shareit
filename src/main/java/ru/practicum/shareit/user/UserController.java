package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto dto) {
        log.info("Создание пользователя: {}", dto);
        User user = userService.create(UserMapper.toUser(dto));
        return UserMapper.toUserDto(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id,
                          @RequestBody UserDto dto) {
        log.info("Обновление пользователя {}: {}", id, dto);
        User user = userService.update(id, UserMapper.toUser(dto));
        return UserMapper.toUserDto(user);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        log.info("Получение пользователя {}", id);
        return UserMapper.toUserDto(userService.get(id));
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Получение всех пользователей");
        return userService.getAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Удаление пользователя {}", id);
        userService.delete(id);
    }
}
