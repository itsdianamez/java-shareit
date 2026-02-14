package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void serialize_shouldWriteCorrectJson() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Laptop");
        dto.setDescription("Black");
        dto.setAvailable(true);

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Laptop");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Black");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void deserialize_shouldReadCorrectJson() throws Exception {
        String content = """
                {
                  "id": 1,
                  "name": "Laptop",
                  "description": "Black",
                  "available": true
                }
                """;

        ItemDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Laptop");
        assertThat(dto.getDescription()).isEqualTo("Black");
        assertThat(dto.getAvailable()).isTrue();
    }
}
