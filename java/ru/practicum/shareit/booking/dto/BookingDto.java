package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
public class BookingDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}