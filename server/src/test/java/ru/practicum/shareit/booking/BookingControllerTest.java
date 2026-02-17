package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setup() {
        User owner = new User();
        owner.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);

        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void create_shouldReturnBooking() throws Exception {
        when(bookingService.create(eq(2L), any(BookingDto.class)))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void approve_shouldApproveBooking() throws Exception {
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingService.approve(1L, 1L, true))
                .thenReturn(booking);

        mockMvc.perform(patch("/bookings/{id}", 1L)
                        .header(USER_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void get_shouldReturnBooking() throws Exception {
        when(bookingService.get(2L, 1L))
                .thenReturn(booking);

        mockMvc.perform(get("/bookings/{id}", 1L)
                        .header(USER_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void userBookings_shouldReturnList() throws Exception {
        when(bookingService.getUserBookings(2L))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void ownerBookings_shouldReturnList() throws Exception {
        when(bookingService.getOwnerBookings(1L))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
