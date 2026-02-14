package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class BaseClient {

    protected final String serverUrl;
    protected final RestTemplate rest;

    public BaseClient(String serverUrl, RestTemplate rest) {
        this.serverUrl = serverUrl;
        this.rest = rest;
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, T body) {
        return exchange(path, HttpMethod.POST, userId, body, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return exchange(path, HttpMethod.POST, null, body, null);
    }


    protected <T> ResponseEntity<Object> patch(String path, Long userId, T body) {
        return exchange(path, HttpMethod.PATCH, userId, body, null);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return exchange(path, HttpMethod.PATCH, null, body, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return exchange(path, HttpMethod.GET, userId, null, null);
    }

    protected ResponseEntity<Object> get(String path) {
        return exchange(path, HttpMethod.GET, null, null, null);
    }

    protected ResponseEntity<Object> delete(String path) {
        return exchange(path, HttpMethod.DELETE, null, null, null);
    }

    private <T> ResponseEntity<Object> exchange(
            String path,
            HttpMethod method,
            Long userId,
            T body,
            Map<String, Object> parameters) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<T> requestEntity = new HttpEntity<>(body, headers);

        try {
            if (parameters != null) {
                return rest.exchange(
                        serverUrl + path,
                        method,
                        requestEntity,
                        Object.class,
                        parameters
                );
            } else {
                return rest.exchange(
                        serverUrl + path,
                        method,
                        requestEntity,
                        Object.class
                );
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsByteArray());
        }
    }
}
