package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingState;

import static org.assertj.core.api.Assertions.assertThat;

class BookingStateTest {

    @Test
    void shouldContainAllValues() {
        assertThat(BookingState.valueOf("ALL"))
                .isEqualTo(BookingState.ALL);

        assertThat(BookingState.values()).hasSize(6);
    }
}
