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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    void setUp() {
        owner = userService.create(makeUser("Owner", "owner@mail.com"));
        booker = userService.create(makeUser("Booker", "booker@mail.com"));

        item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Desc", true));
    }

    @Test
    void create_shouldCreateBooking() {
        Booking booking = bookingService.create(
                booker.getId(),
                makeBookingDto()
        );

        assertNotNull(booking.getId());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void createBooking_shouldIncreaseCount() {
        User user = userService.create(makeUser("User", "user@mail.com"));
        BookingDto dto = makeBookingDto();

        Booking booking = bookingService.create(user.getId(), dto);

        assertNotNull(booking.getId());
    }

    @Test
    void create_shouldThrow_whenStartInPast() {
        BookingDto dto = validDto();
        dto.setStart(LocalDateTime.now().minusDays(1));

        assertThrows(BadRequestException.class,
                () -> bookingService.create(booker.getId(), dto));
    }

    @Test
    void create_shouldThrow_whenStartNull() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(BadRequestException.class,
                () -> bookingService.create(booker.getId(), dto));
    }

    @Test
    void create_shouldThrow_whenEndNull() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));

        assertThrows(BadRequestException.class,
                () -> bookingService.create(booker.getId(), dto));
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
        Booking booking = bookingService.create(booker.getId(), makeBookingDto());

        Booking approved = bookingService.approve(
                owner.getId(),
                booking.getId(),
                true
        );

        assertEquals(BookingStatus.APPROVED, approved.getStatus());
    }

    @Test
    void approve_shouldRejectBooking() {
        Booking booking = bookingService.create(booker.getId(), validDto());

        Booking rejected =
                bookingService.approve(owner.getId(), booking.getId(), false);

        assertEquals(BookingStatus.REJECTED, rejected.getStatus());
    }

    @Test
    void approve_shouldThrow_whenNotOwner() {
        Booking booking = bookingService.create(booker.getId(), makeBookingDto());

        assertThrows(ForbiddenException.class,
                () -> bookingService.approve(booker.getId(), booking.getId(), true));
    }

    @Test
    void approve_shouldThrow_whenBookingNotFound() {
        assertThrows(NotFoundException.class,
                () -> bookingService.approve(owner.getId(), 999L, true));
    }

    @Test
    void get_shouldReturnBooking_forOwner() {
        Booking booking = bookingService.create(booker.getId(), makeBookingDto());

        Booking result = bookingService.get(owner.getId(), booking.getId());

        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void get_shouldReturnBooking_forBooker() {
        Booking booking = bookingService.create(booker.getId(), validDto());

        Booking result =
                bookingService.get(booker.getId(), booking.getId());

        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void get_shouldThrow_whenBookingNotFound() {
        assertThrows(NotFoundException.class,
                () -> bookingService.get(owner.getId(), 999L));
    }

    @Test
    void get_shouldThrow_whenNoAccess() {
        User stranger = userService.create(makeUser("Stranger", "s@mail.com"));
        Booking booking = bookingService.create(booker.getId(), makeBookingDto());

        assertThrows(ForbiddenException.class,
                () -> bookingService.get(stranger.getId(), booking.getId()));
    }

    @Test
    void getUserBookings_shouldReturnList() {
        bookingService.create(booker.getId(), validDto());

        List<Booking> bookings =
                bookingService.getUserBookings(booker.getId());

        assertEquals(1, bookings.size());
    }

    @Test
    void getOwnerBookings_shouldReturnList() {
        bookingService.create(booker.getId(), validDto());

        List<Booking> bookings =
                bookingService.getOwnerBookings(owner.getId());

        assertEquals(1, bookings.size());
    }

    private BookingDto validDto() {
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

    private ItemDto makeItemDto(String name, String desc, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(desc);
        dto.setAvailable(available);
        return dto;
    }

    private BookingDto makeBookingDto() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(1));
        dto.setEnd(LocalDateTime.now().plusDays(1));
        return dto;
    }
}
