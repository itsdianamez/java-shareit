package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void handleNotFound_shouldReturnMessage() {
        NotFoundException ex = new NotFoundException("Not found");

        Map<String, String> result = handler.handleNotFound(ex);

        assertEquals("Not found", result.get("error"));
    }

    @Test
    void handleForbidden_shouldReturnMessage() {
        ForbiddenException ex = new ForbiddenException("Forbidden");

        Map<String, String> result = handler.handleForbidden(ex);

        assertEquals("Forbidden", result.get("error"));
    }

    @Test
    void handleBadRequest_shouldReturnMessage() {
        BadRequestException ex = new BadRequestException("Bad");

        Map<String, String> result = handler.handleBadRequest(ex);

        assertEquals("Bad", result.get("error"));
    }

    @Test
    void exceptions_shouldStoreMessage() {
        assertEquals("Bad",
                new BadRequestException("Bad").getMessage());

        assertEquals("Forbidden",
                new ForbiddenException("Forbidden").getMessage());

        assertEquals("NotFound",
                new NotFoundException("NotFound").getMessage());
    }

}

