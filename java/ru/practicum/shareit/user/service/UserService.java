package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(Long id, User user);

    User get(Long id);

    List<User> getAll();

    void delete(Long id);
}