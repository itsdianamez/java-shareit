package ru.practicum.shareit.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BookingShortDto {

    private Long id;
    private Long bookerId;

    public BookingShortDto(Long id, Long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}