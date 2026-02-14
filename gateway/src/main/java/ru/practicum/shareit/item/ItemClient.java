package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.dto.CommentRequestDto;
import ru.practicum.shareit.dto.ItemDto;

@Component
public class ItemClient extends BaseClient {

    public ItemClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplate rest) {
        super(serverUrl + "/items", rest);
    }

    public ResponseEntity<Object> create(Long userId, ItemDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, ItemDto dto) {
        return patch("/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> get(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAll(Long userId, int from, int size) {
        return get("?from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> search(Long userId, String text, int from, int size) {
        return get("/search?text=" + text + "&from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentRequestDto dto) {
        return post("/" + itemId + "/comment", userId, dto);
    }
}
