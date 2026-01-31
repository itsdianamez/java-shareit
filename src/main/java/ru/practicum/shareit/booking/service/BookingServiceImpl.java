package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    public Booking create(Long userId, BookingDto dto) {
        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new BadRequestException("Дата начала или окончания бронирования не указана");
        }

        if (!dto.getEnd().isAfter(dto.getStart())) {
            throw new BadRequestException("Дата конца бронирования должна быть после даты начала");
        }

        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата начала не может быть в прошлом");
        }

        User booker = userService.get(userId);

        Item item = itemService.getItem(dto.getItemId());

        if (!item.getAvailable()) {
            log.warn("Вещь {} недоступна для бронирования", dto.getItemId());
            throw new BadRequestException("Вещь недоступна");
        }

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    public Booking approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            log.warn("Пользователь {} не является владельцем бронирования {}", ownerId, bookingId);
            throw new ForbiddenException("Только владелец может подтвердить бронирование");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    public Booking get(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("Пользователь {} не имеет доступа к бронированию {}", userId, bookingId);
            throw new ForbiddenException("Доступ ограничен");
        }
        return booking;
    }

    public List<Booking> getUserBookings(Long userId) {
        log.info("Получение всех бронирований пользователя {}", userId);
        return bookingRepository.findByBookerId(userId, Sort.by(Sort.Direction.DESC, "start"));
    }

    public List<Booking> getOwnerBookings(Long ownerId) {
        log.info("Получение всех бронирований владельца {}", ownerId);
        userService.get(ownerId);
        return bookingRepository.findByItemOwnerId(ownerId, Sort.by(Sort.Direction.DESC, "start"));
    }
}