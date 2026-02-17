package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void toDto_shouldMapCorrectly() {
        User author = new User();
        author.setName("User");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Text");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        CommentDto dto = CommentMapper.toDto(comment);

        assertEquals(comment.getId(), dto.getId());
        assertEquals("User", dto.getAuthorName());
        assertEquals(comment.getText(), dto.getText());
    }
}
