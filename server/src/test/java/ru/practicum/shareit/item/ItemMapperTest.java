package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItem_shouldMapCorrectly() {
        ItemDto dto = new ItemDto();
        dto.setName("Laptop");
        dto.setDescription("Black");
        dto.setAvailable(true);

        User owner = new User();
        owner.setId(1L);

        Item item = ItemMapper.toItem(dto, owner);

        assertEquals(dto.getName(), item.getName());
        assertEquals(owner, item.getOwner());
    }

    @Test
    void toItemDto_shouldMapCorrectly() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Laptop");
        item.setDescription("Black");
        item.setAvailable(true);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
    }
}
