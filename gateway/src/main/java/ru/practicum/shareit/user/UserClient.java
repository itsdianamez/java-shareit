package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.dto.UserDto;

@Component
public class UserClient extends BaseClient {

    public UserClient(
            @Value("${shareit-server.url}") String serverUrl,
            RestTemplate restTemplate
    ) {
        super(serverUrl + "/users", restTemplate);
    }

    public ResponseEntity<Object> create(UserDto dto) {
        return post("", dto);
    }

    public ResponseEntity<Object> update(Long id, UserDto dto) {
        return patch("/" + id, dto);
    }

    public ResponseEntity<Object> get(Long id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> getAll() {
        return get("");
    }

    public ResponseEntity<Object> delete(Long id) {
        return delete("/" + id);
    }
}
