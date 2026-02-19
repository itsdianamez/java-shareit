package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.dto.ItemRequestDto;

@Component
public class ItemRequestClient extends BaseClient {

    public ItemRequestClient(
            @Value("${shareit-server.url}") String serverUrl,
            RestTemplate restTemplate
    ) {
        super(serverUrl + "/requests", restTemplate);
    }

    public ResponseEntity<Object> create(Long userId, ItemRequestDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAll(Long userId, int from, int size) {
        return get("/all?from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}
