package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeAndDeserialize() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Good");
        dto.setAuthorName("Name");
        dto.setCreated(LocalDateTime.of(2024, 1, 1, 12, 0));

        String json = objectMapper.writeValueAsString(dto);

        CommentDto result =
                objectMapper.readValue(json, CommentDto.class);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Good");
        assertThat(result.getAuthorName()).isEqualTo("Name");
        assertThat(result.getCreated()).isEqualTo(dto.getCreated());
    }

    @Test
    void serialize_shouldFormatDateCorrectly() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Good");
        dto.setAuthorName("Author");
        dto.setCreated(LocalDateTime.of(2026, 1, 1, 12, 0));

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.created");
        assertThat(result.getJson())
                .contains("\"created\":\"2026-01-01T12:00:00\"");
    }
}
