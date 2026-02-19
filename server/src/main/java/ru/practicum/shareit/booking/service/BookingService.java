package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(Long userId, BookingDto dto);

    Booking approve(Long ownerId, Long bookingId, boolean approved);

    Booking get(Long userId, Long bookingId);

    List<Booking> getUserBookings(Long userId);

    List<Booking> getOwnerBookings(Long ownerId);
}