package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelCoverageTest {

    @Test
    void userModel() {
        User user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setEmail("mail@mail.com");

        assertEquals(1L, user.getId());
        assertEquals("Ivan", user.getName());
        assertEquals("mail@mail.com", user.getEmail());
    }

    @Test
    void itemModel() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Laptop");

        assertEquals(1L, item.getId());
        assertEquals("Laptop", item.getName());
    }

    @Test
    void commentModel() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Nice");

        assertEquals(1L, comment.getId());
        assertEquals("Nice", comment.getText());
    }

    @Test
    void requestModel() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need");

        assertEquals(1L, request.getId());
        assertEquals("Need", request.getDescription());
    }
}

