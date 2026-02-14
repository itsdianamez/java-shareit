package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.ItemRequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient client;
    private static final String USER_HEADER = "X-Sharer-User-Id";


    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestBody @Valid ItemRequestDto dto) {
        log.info("Создание запроса пользователем {}: {}", userId, dto);
        return client.create(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader(USER_HEADER) Long userId) {
        log.info("Получение запросов пользователя {}", userId);
        return client.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Получение всех запросов (кроме своих) пользователем {}: from={}, size={}",
                userId, from, size);
        return client.getAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long id) {
        log.info("Получение запроса {} пользователем {}", id, userId);
        return client.getById(userId, id);
    }
}
