package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User stranger;

    @BeforeEach
    void setUp() {
        owner = userService.create(makeUser("Owner", "owner@mail.com"));
        stranger = userService.create(makeUser("Stranger", "s@mail.com"));
    }

    @Test
    void create_shouldCreateItem() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        assertNotNull(item.getId());
    }

    @Test
    void create_shouldThrow_whenUserNotFound() {
        assertThrows(NotFoundException.class,
                () -> itemService.create(999L,
                        makeItemDto("Laptop", "Black", true, null)));
    }

    @Test
    void create_shouldThrow_whenNameBlank() {
        ItemDto dto = makeItemDto("", "Desc", true, null);

        assertThrows(BadRequestException.class,
                () -> itemService.create(owner.getId(), dto));
    }

    @Test
    void create_shouldThrow_whenDescriptionBlank() {
        ItemDto dto = makeItemDto("Name", "", true, null);

        assertThrows(BadRequestException.class,
                () -> itemService.create(owner.getId(), dto));
    }

    @Test
    void create_shouldThrow_whenRequestNotFound() {
        ItemDto dto = makeItemDto("Name", "Desc", true, 999L);

        assertThrows(NotFoundException.class,
                () -> itemService.create(owner.getId(), dto));
    }

    @Test
    void create_shouldThrow_whenOwnerNotFound() {
        assertThrows(NotFoundException.class,
                () -> itemService.create(999L,
                        makeItemDto("Item", "Desc", true, null)));
    }

    @Test
    void get_shouldReturnItem_forOwner() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        ItemWithBookingsDto found =
                itemService.get(owner.getId(), item.getId());

        assertEquals(item.getId(), found.getId());
    }

    @Test
    void get_shouldThrow_whenItemNotFound() {
        assertThrows(NotFoundException.class,
                () -> itemService.get(owner.getId(), 999L));
    }

    @Test
    void getOwnerItems_shouldReturnEmptyList() {
        List<ItemWithBookingsDto> items =
                itemService.getOwnerItems(owner.getId());

        assertTrue(items.isEmpty());
    }

    @Test
    void getOwnerItems_shouldReturnItems() {
        itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        List<ItemWithBookingsDto> items =
                itemService.getOwnerItems(owner.getId());

        assertEquals(1, items.size());
    }

    @Test
    void getOwnerItems_shouldReturnItemWithoutBookingsAndComments() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Item", "Desc", true, null));

        List<ItemWithBookingsDto> result =
                itemService.getOwnerItems(owner.getId());

        ItemWithBookingsDto dto = result.get(0);

        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
        assertTrue(dto.getComments().isEmpty());
    }

    @Test
    void getOwnerItems_shouldReturnWithComments() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Item", "Desc", true, null));

        bookingRepository.save(
                Booking.builder()
                        .item(item)
                        .booker(stranger)
                        .start(LocalDateTime.now().minusDays(2))
                        .end(LocalDateTime.now().minusDays(1))
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        itemService.addComment(stranger.getId(), item.getId(), "Nice");

        List<ItemWithBookingsDto> result =
                itemService.getOwnerItems(owner.getId());

        assertEquals(1, result.get(0).getComments().size());
    }

    @Test
    void getOwnerItems_shouldFillLastAndNext() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Item", "Desc", true, null));

        bookingRepository.save(
                Booking.builder()
                        .item(item)
                        .booker(stranger)
                        .start(LocalDateTime.now().minusDays(5))
                        .end(LocalDateTime.now().minusDays(4))
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        bookingRepository.save(
                Booking.builder()
                        .item(item)
                        .booker(stranger)
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(2))
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        List<ItemWithBookingsDto> result =
                itemService.getOwnerItems(owner.getId());

        ItemWithBookingsDto dto = result.get(0);

        assertNotNull(dto.getLastBooking());
        assertNotNull(dto.getNextBooking());
    }

    @Test
    void get_shouldReturnWithoutBookings_whenNotOwner() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        ItemWithBookingsDto dto =
                itemService.get(stranger.getId(), item.getId());

        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
    }

    @Test
    void update_shouldChangeFields() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        ItemDto update = new ItemDto();
        update.setName("Phone");

        Item updated =
                itemService.update(owner.getId(), item.getId(), update);

        assertEquals("Phone", updated.getName());
    }

    @Test
    void update_shouldThrow_whenNotOwner() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        ItemDto update = new ItemDto();
        update.setName("Phone");

        assertThrows(ForbiddenException.class,
                () -> itemService.update(stranger.getId(), item.getId(), update));
    }

    @Test
    void update_shouldThrow_whenItemNotFound() {
        assertThrows(NotFoundException.class,
                () -> itemService.update(owner.getId(), 999L,
                        makeItemDto("Phone", "Desc", true, null)));
    }

    @Test
    void update_shouldUpdateOnlyName() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Old", "Desc", true, null));

        ItemDto update = new ItemDto();
        update.setName("New");

        Item updated = itemService.update(owner.getId(), item.getId(), update);

        assertEquals("New", updated.getName());
        assertEquals("Desc", updated.getDescription());
        assertTrue(updated.getAvailable());
    }

    @Test
    void update_shouldUpdateAllFields() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Old", "Desc", true, null));

        ItemDto update = new ItemDto();
        update.setName("New");
        update.setDescription("NewDesc");
        update.setAvailable(false);

        Item updated =
                itemService.update(owner.getId(), item.getId(), update);

        assertEquals("New", updated.getName());
        assertEquals("NewDesc", updated.getDescription());
        assertFalse(updated.getAvailable());
    }

    @Test
    void search_shouldReturnMatchingItems() {
        itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        List<Item> found = itemService.search("laptop");

        assertEquals(1, found.size());
    }

    @Test
    void search_shouldReturnEmpty_whenBlankText() {
        List<Item> found = itemService.search("");

        assertTrue(found.isEmpty());
    }

    @Test
    void search_shouldReturnEmpty_whenNull() {
        assertTrue(itemService.search(null).isEmpty());
    }

    @Test
    void search_shouldReturnEmpty_whenBlankSpaces() {
        assertTrue(itemService.search("   ").isEmpty());
    }

    @Test
    void addComment_shouldThrow_whenNoBooking() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        assertThrows(BadRequestException.class,
                () -> itemService.addComment(stranger.getId(),
                        item.getId(),
                        "Nice"));
    }

    @Test
    void addComment_shouldCreateComment_whenBookingExists() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        Booking booking = bookingRepository.save(
                Booking.builder()
                        .item(item)
                        .booker(stranger)
                        .start(LocalDateTime.now().minusDays(2))
                        .end(LocalDateTime.now().minusDays(1))
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        Comment comment = itemService.addComment(
                stranger.getId(),
                item.getId(),
                "Great item"
        );

        assertEquals("Great item", comment.getText());
    }

    @Test
    void addComment_shouldThrow_whenUserNotFound() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        bookingRepository.save(
                Booking.builder()
                        .item(item)
                        .booker(stranger)
                        .start(LocalDateTime.now().minusDays(2))
                        .end(LocalDateTime.now().minusDays(1))
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        userService.delete(stranger.getId());

        assertThrows(NotFoundException.class,
                () -> itemService.addComment(stranger.getId(),
                        item.getId(),
                        "Nice"));
    }

    @Test
    void getItem_shouldReturnItem() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        Item found = itemService.getItem(item.getId());

        assertEquals(item.getId(), found.getId());
    }

    @Test
    void getItem_shouldThrow_whenNotFound() {
        assertThrows(NotFoundException.class,
                () -> itemService.getItem(999L));
    }

    @Test
    void get_shouldFillLastAndNextBooking() {
        Item item = itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", true, null));

        bookingRepository.save(
                Booking.builder()
                        .item(item)
                        .booker(stranger)
                        .start(LocalDateTime.now().minusDays(5))
                        .end(LocalDateTime.now().minusDays(4))
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        bookingRepository.save(
                Booking.builder()
                        .item(item)
                        .booker(stranger)
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(2))
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        ItemWithBookingsDto dto =
                itemService.get(owner.getId(), item.getId());

        assertNotNull(dto.getLastBooking());
        assertNotNull(dto.getNextBooking());
    }

    @Test
    void search_shouldReturnEmpty_whenNullText() {
        List<Item> found = itemService.search(null);
        assertTrue(found.isEmpty());
    }

    @Test
    void search_shouldIgnoreUnavailable() {
        itemService.create(owner.getId(),
                makeItemDto("Laptop", "Black", false, null));

        List<Item> found = itemService.search("lap");

        assertTrue(found.isEmpty());
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private ItemDto makeItemDto(String name, String desc,
                                Boolean available, Long requestId) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(desc);
        dto.setAvailable(available);
        dto.setRequestId(requestId);
        return dto;
    }
}
