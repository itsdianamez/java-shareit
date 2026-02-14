package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.BookingCreateDto;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody BookingCreateDto dto) {
        log.info("Создание бронирования пользователем {}: {}", userId, dto);
        return bookingClient.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        log.info("Подтверждение бронирования {} пользователем {}: approved={}",
                bookingId, userId, approved);
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long bookingId) {
        log.info("Получение бронирования {} пользователем {}", bookingId, userId);
        return bookingClient.get(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получение бронирований пользователя {}: state={}, from={}, size={}",
                userId, state, from, size);
        return bookingClient.getAllByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получение бронирований владельца {}: state={}, from={}, size={}",
                userId, state, from, size);
        return bookingClient.getAllByOwner(userId, state, from, size);
    }
}
