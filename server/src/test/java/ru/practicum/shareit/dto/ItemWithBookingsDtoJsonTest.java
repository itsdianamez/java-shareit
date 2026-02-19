package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemWithBookingsDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeAndDeserialize() throws Exception {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(1L);
        dto.setName("Item");
        dto.setDescription("Desc");
        dto.setAvailable(true);

        dto.setLastBooking(new BookingShortDto(10L, 2L));
        dto.setNextBooking(new BookingShortDto(11L, 3L));

        CommentDto comment = new CommentDto();
        comment.setId(5L);
        comment.setText("Good");

        dto.setComments(List.of(comment));

        String json = objectMapper.writeValueAsString(dto);

        ItemWithBookingsDto result =
                objectMapper.readValue(json, ItemWithBookingsDto.class);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLastBooking().getId()).isEqualTo(10L);
        assertThat(result.getComments()).hasSize(1);
    }
}
