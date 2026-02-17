package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toUser_shouldMapCorrectly() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("User");
        dto.setEmail("mail@mail.com");

        User user = UserMapper.toUser(dto);

        assertEquals(dto.getId(), user.getId());
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }

    @Test
    void toUserDto_shouldMapCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("mail@mail.com");

        UserDto dto = UserMapper.toUserDto(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }
}
