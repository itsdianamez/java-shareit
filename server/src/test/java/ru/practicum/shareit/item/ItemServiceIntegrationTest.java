package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserService userService;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(makeUser("Owner", "owner@mail.com"));
    }

    @Test
    void create_shouldCreateItem() {
        ItemDto dto = new ItemDto();
        dto.setName("Laptop");
        dto.setDescription("Black");
        dto.setAvailable(true);

        Item item = itemService.create(owner.getId(), dto);

        assertNotNull(item.getId());
        assertEquals("Laptop", item.getName());
        assertEquals(owner.getId(), item.getOwner().getId());
    }

    @Test
    void create_shouldSaveItem() {
        ItemDto dto = makeItemDto("Laptop", "Black", true, null);

        Item item = itemService.create(owner.getId(), dto);

        assertNotNull(item.getId());
        assertEquals("Laptop", item.getName());
        assertEquals(owner.getId(), item.getOwner().getId());
    }

    @Test
    void get_shouldReturnItem() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        ItemWithBookingsDto found = itemService.get(owner.getId(), item.getId());

        assertEquals(item.getId(), found.getId());
    }

    @Test
    void update_shouldChangeFields() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        ItemDto update = new ItemDto();
        update.setName("Phone");

        Item updated = itemService.update(owner.getId(), item.getId(), update);

        assertEquals("Phone", updated.getName());
    }

    @Test
    void search_shouldReturnMatchingItems() {
        itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        List<Item> found = itemService.search("laptop");

        assertEquals(1, found.size());
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private ItemDto makeItemDto(String name, String desc, Boolean available, Long requestId) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(desc);
        dto.setAvailable(available);
        dto.setRequestId(requestId);
        return dto;
    }
}

