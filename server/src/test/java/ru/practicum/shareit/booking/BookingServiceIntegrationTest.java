package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setup() {
        owner = userService.create(makeUser("Owner", "owner@mail.com"));
        booker = userService.create(makeUser("Booker", "booker@mail.com"));

        ItemDto dto = new ItemDto();
        dto.setName("Laptop");
        dto.setDescription("Black");
        dto.setAvailable(true);

        item = itemService.create(owner.getId(), dto);
    }

    @Test
    void create_shouldCreateBooking() {
        BookingDto dto = makeValidBookingDto();

        Booking booking = bookingService.create(booker.getId(), dto);

        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void create_shouldThrow_whenEndBeforeStart() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(2));
        dto.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(BadRequestException.class,
                () -> bookingService.create(booker.getId(), dto));
    }

    @Test
    void approve_shouldApproveBooking() {
        Booking booking = bookingService.create(booker.getId(), makeValidBookingDto());

        Booking approved = bookingService.approve(owner.getId(), booking.getId(), true);

        assertEquals(BookingStatus.APPROVED, approved.getStatus());
    }

    @Test
    void approve_shouldThrow_whenNotOwner() {
        Booking booking = bookingService.create(booker.getId(), makeValidBookingDto());

        assertThrows(ForbiddenException.class,
                () -> bookingService.approve(booker.getId(), booking.getId(), true));
    }

    @Test
    void get_shouldThrow_whenNoAccess() {
        Booking booking = bookingService.create(booker.getId(), makeValidBookingDto());
        User stranger = userService.create(makeUser("Stranger", "s@mail.com"));

        assertThrows(ForbiddenException.class,
                () -> bookingService.get(stranger.getId(), booking.getId()));
    }

    private BookingDto makeValidBookingDto() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        return dto;
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}

