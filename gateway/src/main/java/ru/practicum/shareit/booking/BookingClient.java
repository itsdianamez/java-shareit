package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.dto.BookingCreateDto;

@Component
public class BookingClient extends BaseClient {

    public BookingClient(
            @Value("${shareit-server.url}") String serverUrl,
            RestTemplate restTemplate
    ) {
        super(serverUrl + "/bookings", restTemplate);
    }

    public ResponseEntity<Object> create(Long userId, BookingCreateDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> approve(Long userId, Long bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId, null);
    }

    public ResponseEntity<Object> get(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllByUser(Long userId, String state, int from, int size) {
        return get("?state=" + state + "&from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> getAllByOwner(Long userId, String state, int from, int size) {
        return get("/owner?state=" + state + "&from=" + from + "&size=" + size, userId);
    }
}
