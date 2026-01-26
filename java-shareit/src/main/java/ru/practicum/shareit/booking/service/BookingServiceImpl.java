package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public Booking create(Long userId, BookingDto dto) {
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() ->
                        new RuntimeException(new ChangeSetPersister.NotFoundException())
                );

        if (!item.getAvailable()) {
            log.warn("Вещь {} недоступна для бронирования", dto.getItemId());
            throw new IllegalStateException();
        }

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(userRepository.findById(userId).orElseThrow());
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    public Booking approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            log.warn("Пользователь {} не является владельцем бронирования {}", ownerId, bookingId);
            throw new IllegalStateException();
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    public Booking get(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("Пользователь {} не имеет доступа к бронированию {}", userId, bookingId);
            throw new IllegalStateException();
        }
        return booking;
    }

    public List<Booking> getUserBookings(Long userId) {
        log.info("Получение всех бронирований пользователя {}", userId);
        return bookingRepository.findByBookerId(userId, Sort.by(Sort.Direction.DESC, "start"));
    }

    public List<Booking> getOwnerBookings(Long ownerId) {
        log.info("Получение всех бронирований владельца {}", ownerId);
        userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return bookingRepository.findByItemOwnerId(ownerId, Sort.by(Sort.Direction.DESC, "start"));
    }
}