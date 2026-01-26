package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public Booking create(@RequestHeader(USER_HEADER) Long userId,
                          @RequestBody BookingDto dto) {
        log.info("Создание бронирования пользователем {}: {}", userId, dto);
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{id}")
    public Booking approve(@RequestHeader(USER_HEADER) Long userId,
                           @PathVariable Long id,
                           @RequestParam boolean approved) {
        log.info("Пользователь {} {} бронирование {}", userId, approved ? "одобрил" : "отклонил", id);
        return bookingService.approve(userId, id, approved);
    }

    @GetMapping("/{id}")
    public Booking get(@RequestHeader(USER_HEADER) Long userId,
                       @PathVariable Long id) {
        log.info("Получение бронирования {} пользователем {}", id, userId);
        return bookingService.get(userId, id);
    }

    @GetMapping
    public List<Booking> userBookings(@RequestHeader(USER_HEADER) Long userId) {
        log.info("Получение всех бронирований пользователя {}", userId);
        return bookingService.getUserBookings(userId);
    }

    @GetMapping("/owner")
    public List<Booking> ownerBookings(@RequestHeader(USER_HEADER) Long userId) {
        log.info("Получение всех бронирований владельца {}", userId);
        return bookingService.getOwnerBookings(userId);
    }
}