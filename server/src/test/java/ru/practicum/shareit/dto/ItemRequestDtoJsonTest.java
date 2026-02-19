package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeAndDeserialize() throws Exception {
        ItemResponseDto response =
                new ItemResponseDto(1L, "Item", 2L);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Need laptop");
        dto.setCreated(LocalDateTime.of(2024, 1, 1, 10, 0));
        dto.setItems(List.of(response));

        String json = objectMapper.writeValueAsString(dto);

        ItemRequestDto result =
                objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getItems()).hasSize(1);
    }
}
