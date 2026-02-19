package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingShortDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeAndDeserialize() throws Exception {
        BookingShortDto dto = new BookingShortDto(1L, 2L);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"bookerId\":2");

        BookingShortDto result =
                objectMapper.readValue(json, BookingShortDto.class);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBookerId()).isEqualTo(2L);
    }
}
