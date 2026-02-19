package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.item.dto.CommentRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeAndDeserialize() throws Exception {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("Test");

        String json = objectMapper.writeValueAsString(dto);

        CommentRequestDto result =
                objectMapper.readValue(json, CommentRequestDto.class);

        assertThat(result.getText()).isEqualTo("Test");
    }
}
