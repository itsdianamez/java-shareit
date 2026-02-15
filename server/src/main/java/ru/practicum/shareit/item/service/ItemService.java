package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Long ownerId, ItemDto dto);

    Item update(Long ownerId, Long itemId, ItemDto dto);

    ItemWithBookingsDto get(Long userId, Long itemId);

    List<ItemWithBookingsDto> getOwnerItems(Long ownerId);

    List<Item> search(String text);

    Comment addComment(Long userId, Long itemId, String text);

    Item getItem(Long itemId);
}