package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.CommentRequestDto;
import ru.practicum.shareit.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {

    private final ItemClient itemClient;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody ItemDto dto) {
        log.info("Создание вещи пользователем {}: {}", userId, dto);
        return itemClient.create(userId, dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long id,
            @RequestBody ItemDto dto) {
        log.info("Обновление вещи {} пользователем {}: {}", id, userId, dto);
        return itemClient.update(userId, id, dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long id) {
        log.info("Получение вещи {} пользователем {}", id, userId);
        return itemClient.get(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получение вещей пользователя {}: from={}, size={}",
                userId, from, size);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Поиск вещей пользователем {}: text='{}', from={}, size={}",
                userId, text, from, size);
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long id,
            @Valid @RequestBody CommentRequestDto dto) {
        log.info("Добавление комментария к вещи {} пользователем {}: {}",
                id, userId, dto);
        return itemClient.addComment(userId, id, dto);
    }
}
