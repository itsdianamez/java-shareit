package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public Item create(@RequestHeader(USER_HEADER) Long userId,
                       @RequestBody ItemDto dto) {
        log.info("Создание вещи пользователем {}: {}", userId, dto);
        return itemService.create(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader(USER_HEADER) Long userId,
                       @PathVariable Long itemId,
                       @RequestBody ItemDto dto) {
        log.info("Обновление вещи {} пользователем {}: {}", itemId, userId, dto);
        return itemService.update(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public Object get(@RequestHeader(USER_HEADER) Long userId,
                      @PathVariable Long itemId) {
        log.info("Получение вещи {} пользователем {}", itemId, userId);
        return itemService.get(userId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> ownerItems(@RequestHeader(USER_HEADER) Long userId) {
        log.info("Получение всех вещей владельца {}", userId);
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam String text) {
        log.info("Поиск вещей по тексту: '{}'", text);
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentRequestDto dto) {
        log.info("Добавление комментария к вещи {} пользователем {}: {}", itemId, userId, dto.getText());
        return CommentMapper.toDto(
                itemService.addComment(userId, itemId, dto.getText())
        );
    }
}