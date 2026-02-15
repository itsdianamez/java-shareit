package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_HEADER) Long userId,
                                 @RequestBody @Valid ItemRequestCreateDto dto) {
        log.info("Создание запроса пользователем {}: {}", userId, dto);
        return service.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(
            @RequestHeader(USER_HEADER) Long userId) {
        log.info("Получение запросов пользователя {}", userId);
        return service.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(
            @RequestHeader(USER_HEADER) Long userId) {
        log.info("Получение всех запросов (кроме своих) пользователем {}", userId);
        return service.getOtherUsersRequests(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto get(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long id) {
        log.info("Получение запроса {} пользователем {}", id, userId);
        return service.getRequest(userId, id);
    }
}
