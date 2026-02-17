package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private User owner;
    private User stranger;

    @BeforeEach
    void setUp() {
        owner = userService.create(makeUser("Owner", "owner@mail.com"));
        stranger = userService.create(makeUser("Stranger", "s@mail.com"));
    }

    @Test
    void create_shouldCreateItem() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        assertNotNull(item.getId());
    }

    @Test
    void create_shouldThrow_whenUserNotFound() {
        assertThrows(NotFoundException.class,
                () -> itemService.create(999L,
                        makeItemDto("Laptop", "Black", true, null)));
    }

    @Test
    void get_shouldReturnItem_forOwner() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        ItemWithBookingsDto found =
                itemService.get(owner.getId(), item.getId());

        assertEquals(item.getId(), found.getId());
    }

    @Test
    void get_shouldThrow_whenItemNotFound() {
        assertThrows(NotFoundException.class,
                () -> itemService.get(owner.getId(), 999L));
    }

    @Test
    void update_shouldChangeFields() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        ItemDto update = new ItemDto();
        update.setName("Phone");

        Item updated =
                itemService.update(owner.getId(), item.getId(), update);

        assertEquals("Phone", updated.getName());
    }

    @Test
    void update_shouldThrow_whenNotOwner() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        ItemDto update = new ItemDto();
        update.setName("Phone");

        assertThrows(ForbiddenException.class,
                () -> itemService.update(stranger.getId(), item.getId(), update));
    }

    @Test
    void update_shouldThrow_whenItemNotFound() {
        assertThrows(NotFoundException.class,
                () -> itemService.update(owner.getId(), 999L,
                        makeItemDto("Phone", "Desc", true, null)));
    }

    @Test
    void search_shouldReturnMatchingItems() {
        itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        List<Item> found = itemService.search("laptop");

        assertEquals(1, found.size());
    }

    @Test
    void search_shouldReturnEmpty_whenBlankText() {
        List<Item> found = itemService.search("");

        assertTrue(found.isEmpty());
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private ItemDto makeItemDto(String name, String desc,
                                Boolean available, Long requestId) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(desc);
        dto.setAvailable(available);
        dto.setRequestId(requestId);
        return dto;
    }
}
