package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Laptop");
        item.setDescription("Black");
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setName("Laptop");
        itemDto.setDescription("Black");
        itemDto.setAvailable(true);
    }

    @Test
    void create_shouldReturnItem() throws Exception {
        when(itemService.create(eq(1L), any(ItemDto.class)))
                .thenReturn(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void get_shouldReturnItem() throws Exception {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(1L);
        dto.setName("Laptop");

        when(itemService.get(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getOwnerItems_shouldReturnList() throws Exception {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(1L);
        dto.setName("Laptop");

        when(itemService.getOwnerItems(1L))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void update_shouldReturnUpdatedItem() throws Exception {
        item.setName("Phone");

        when(itemService.update(eq(1L), eq(1L), any(ItemDto.class)))
                .thenReturn(item);

        mockMvc.perform(patch("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    @Test
    void search_shouldReturnItems() throws Exception {
        when(itemService.search("laptop"))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("text", "laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

}
