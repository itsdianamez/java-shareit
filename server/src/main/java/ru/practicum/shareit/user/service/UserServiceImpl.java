package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public User create(User user) {
        log.info("Создание пользователя: {}", user);
        User savedUser = userRepository.save(user);
        log.info("Пользователь {} успешно создан", savedUser.getId());
        return savedUser;
    }

    public User update(Long id, User user) {
        log.info("Обновление пользователя {}: {}", id, user);
        User existing = get(id);
        if (user.getName() != null) existing.setName(user.getName());
        if (user.getEmail() != null) existing.setEmail(user.getEmail());
        User updatedUser = userRepository.save(existing);
        log.info("Пользователь {} успешно обновлен", updatedUser.getId());
        return updatedUser;
    }

    public User get(Long id) {
        log.info("Получение пользователя {}", id);
        User found = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь {} не найден", id);
                    return new NotFoundException("Пользователь не найден");
                });
        log.info("Пользователь {} успешно найден", id);
        return found;
    }

    public List<User> getAll() {
        log.info("Получение всех пользователей");
        List<User> users = userRepository.findAll();
        log.info("Найдено {} пользователей", users.size());
        return users;
    }

    public void delete(Long id) {
        log.info("Удаление пользователя {}", id);
        userRepository.deleteById(id);
        log.info("Пользователь {} успешно удален", id);
    }
}