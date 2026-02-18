package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serialize_shouldWriteCorrectJson() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2026, 1, 1, 12, 0));
        dto.setEnd(LocalDateTime.of(2026, 1, 2, 12, 0));

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);

        assertThat(result.getJson())
                .contains("\"start\":\"2026-01-01T12:00:00\"");
        assertThat(result.getJson())
                .contains("\"end\":\"2026-01-02T12:00:00\"");
    }

    @Test
    void deserialize_shouldReadCorrectJson() throws Exception {

        String content = "{\n" +
                "  \"itemId\": 1,\n" +
                "  \"start\": \"2026-01-01T12:00:00\",\n" +
                "  \"end\": \"2026-01-02T12:00:00\"\n" +
                "}";

        BookingDto dto = json.parseObject(content);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart())
                .isEqualTo(LocalDateTime.of(2026, 1, 1, 12, 0));
        assertThat(dto.getEnd())
                .isEqualTo(LocalDateTime.of(2026, 1, 2, 12, 0));
    }
}
