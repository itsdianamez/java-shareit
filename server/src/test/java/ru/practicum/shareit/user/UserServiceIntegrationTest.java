package ru.practicum.shareit.user;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @Test
    void create_shouldSaveUser() {
        User user = makeUser("User", "user@mail.com");

        User saved = userService.create(user);

        assertNotNull(saved.getId());
        assertEquals("User", saved.getName());
        assertEquals("user@mail.com", saved.getEmail());
    }

    @Test
    void get_shouldReturnUser() {
        User saved = userService.create(makeUser("User", "user@mail.com"));

        User found = userService.get(saved.getId());

        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void update_shouldChangeFields() {
        User saved = userService.create(makeUser("User", "user@mail.com"));

        User update = new User();
        update.setName("Person");

        User updated = userService.update(saved.getId(), update);

        assertEquals("Person", updated.getName());
        assertEquals("user@mail.com", updated.getEmail());
    }

    @Test
    void delete_shouldRemoveUser() {
        User saved = userService.create(makeUser("User", "user@mail.com"));

        userService.delete(saved.getId());

        assertThrows(NotFoundException.class,
                () -> userService.get(saved.getId()));
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
