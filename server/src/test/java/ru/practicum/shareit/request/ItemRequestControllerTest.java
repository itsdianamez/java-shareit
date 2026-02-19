package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService requestService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestCreateDto createDto;
    private ItemRequestDto responseDto;

    @BeforeEach
    void setup() {
        createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a laptop");

        responseDto = new ItemRequestDto();
        responseDto.setId(1L);
        responseDto.setDescription("Need a laptop");
        responseDto.setCreated(LocalDateTime.now());
    }

    @Test
    void create_shouldReturnRequest() throws Exception {

        when(requestService.create(eq(1L), any(ItemRequestCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a laptop"));
    }

    @Test
    void getUserRequests_shouldReturnList() throws Exception {

        when(requestService.getUserRequests(1L))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getOtherUsersRequests_shouldReturnList() throws Exception {

        when(requestService.getOtherUsersRequests(1L))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getById_shouldReturnRequest() throws Exception {

        when(requestService.getRequest(1L, 1L))
                .thenReturn(responseDto);

        mockMvc.perform(get("/requests/{id}", 1L)
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a laptop"));
    }
}
